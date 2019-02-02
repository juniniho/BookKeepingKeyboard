package com.cy.bookkeepingkeyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 键盘辅助类
 */
public class BookKeyboardHelper {
    private Context mContext;
    private View rootView;

    private MyKeyBoardView mKeyboardView;
    private Keyboard mKeyboardNumber;//数字键盘
    private TextView mTv;
    private Date mDate;
    private LinearLayout ll_bookkeepingkeyboard;
    private TextView txt_date;
    public TextView txt_remark;

    private static final String DEFAULT_VALUE = "0.00";

    public BookKeyboardHelper(Context context,View rootView) {
        this.mContext = context;
        this.rootView = rootView;
        mKeyboardNumber = new Keyboard(mContext, R.xml.keyboard_number);
        ll_bookkeepingkeyboard = rootView.findViewById(R.id.ll_bookkeepingkeyboard);
        mKeyboardView = rootView.findViewById(R.id.keyboard_view);
        mDate = new Date();
        txt_date = rootView.findViewById(R.id.txt_date);
        txt_remark = rootView.findViewById(R.id.txt_remark);
    }

    /**
     * edittext绑定自定义键盘
     *
     * @param editText 需要绑定自定义键盘的edittext
     */
    public void attachTo(TextView editText) {
        this.mTv = editText;
        showSoftKeyboard();
    }

    public void showSoftKeyboard() {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = new Keyboard(mContext, R.xml.keyboard_number);
        }
        if (mKeyboardView == null) {
            mKeyboardView = rootView.findViewById(R.id.keyboard_view);
        }

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDateClick != null) {
                    mOnDateClick.onDateClick(txt_date.getText().toString());
                }
            }
        });

        if(mKeyboardView == null){
            throw new IllegalArgumentException("使用bookkeepingkeyboard必须在activity include include_keyboardview布局");
        }

        mKeyboardView.setKeyboard(mKeyboardNumber);

        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        ll_bookkeepingkeyboard.setVisibility(View.VISIBLE);
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);

    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Keyboard.Key keyConfirm = getKeyByKeyCode(Keyboard.KEYCODE_DONE);
            String text = mTv.getText().toString();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退按钮
                if(text.equals(DEFAULT_VALUE)){
                    return;
                }
                if(text.length() == 1) {
                    //如果只剩1位，置0
                    mTv.setText(DEFAULT_VALUE);
                }else {
                    backSpace();
                }
                if(isExpression()){
                    keyConfirm.label = "=";
                }else {
                    keyConfirm.label = "完成";
                }
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {//完成/=
                if(keyConfirm.label.equals("=")){
                    calculate(keyConfirm);
                }else {
                    if(text.matches(".*[\\+-\\.]")){
                        //去除末尾多余的+-.
                        backSpace();
                    }

                    if (mOnOkClick != null) {
                        SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");
                        //使用选择的日期和当前时分秒拼接为时间戳作为排序依据
                        String date = outFormat.format(mDate);
                        Calendar dateCalendar = Calendar.getInstance();
                        dateCalendar.setTime(mDate);
                        Calendar timeCalendar = Calendar.getInstance();
                        timeCalendar.set(Calendar.YEAR,dateCalendar.get(Calendar.YEAR));
                        timeCalendar.set(Calendar.MONTH,dateCalendar.get(Calendar.MONTH));
                        timeCalendar.set(Calendar.DAY_OF_MONTH,dateCalendar.get(Calendar.DAY_OF_MONTH));

                        mOnOkClick.onOkClick(mTv.getText().toString(),date,timeCalendar.getTimeInMillis());
                    }
                }

            }else if(primaryCode == 1002){
                //+
                if(isExpression()){
                    calculate(keyConfirm);
                }

                if(text.endsWith("-")){
                    backSpace();
                }
                if(text.endsWith("+")){
                    return;
                }
                append("+");

            }else if(primaryCode == 1003){
                //-;
                if(isExpression()){
                    calculate(keyConfirm);
                }

                if(text.endsWith("+")){
                    backSpace();
                }
                if(text.endsWith("-")){
                    return;
                }
                append("-");
            }else if(primaryCode == 1004){
                mTv.setText("0");

            } else{
                //数字和点
                String inputChar = Character.toString((char) primaryCode);
                if((text.equals("0") || text.equals(DEFAULT_VALUE))){
                    //如果是默认值，直接替换
                    if(!inputChar.equals(".")) {
                        mTv.setText(inputChar);
                    }else {
                        mTv.setText("0.");
                    }
                    return;
                }

                if(".".equals(inputChar) && (text.endsWith(".") || text.matches(".*\\.[0-9]{1}"))){
                    //只能输入一个点
                    return;
                }

                if(text.matches(".*\\.[0-9]{2}")){
                    //已经输入两位小数，不允许输入数字和点
                    return;
                }
                if(text.matches(".*[\\+-]0") && !inputChar.equals(".")){
                    //+0或者-0结尾不是点号删掉多余的0
                    backSpace();
                }
                if(text.matches(".*[0-9]{5}") && !inputChar.equals(".")){
                    //限制最高几位数
                    return;
                }
                if(text.endsWith("+") || text.endsWith("-")){
                    if(inputChar.equals(".")) {
                        append("0");
                    }
                    keyConfirm.label = "=";
                }

                append(inputChar);


            }


        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    /**
     * 退格
     */
    private void backSpace(){
        String text = mTv.getText().toString();
        if(text.length() > 0) {
            mTv.setText(text.substring(0, text.length() - 1));
        }

    }

    private void append(String str){
        StringBuilder sb = new StringBuilder(mTv.getText().toString());
        sb.append(str);
        mTv.setText(sb.toString());
    }

    /**
     * 是否是运算表达式
     * @return
     */
    private boolean isExpression(){
        String text = mTv.getText().toString();
        return text.matches("-*[\\d\\.]+[\\+-][\\d\\.]+");
    }

    private void calculate(Keyboard.Key keyConfirm){
        String text = mTv.getText().toString();
        if(text.endsWith(".")){
            //忽略末尾的点
            text = text.substring(0,text.length() - 1);
        }
        String s1,s2;
        boolean isAdd;
        int pos = text.lastIndexOf("+");
        if(pos > 0){
            isAdd = true;
        }else {
            isAdd = false;
            pos = text.lastIndexOf("-");
        }
        s1 = text.substring(0,pos);
        s2 = text.substring(pos + 1);
        double d1 = Double.parseDouble(s1);
        double d2 = Double.parseDouble(s2);
        double res;
        if(isAdd){
            res = d1 + d2;
        }else {
            res = d1 - d2;
        }
        mTv.setText(new DecimalFormat("#.##").format(res));
        keyConfirm.label = "完成";
    }

    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    public static void hideSystemSofeKeyboard(Context context, EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void hideSoftInput(Activity activity) {

        if (activity == null) return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }




    public interface OnOkClick {
        void onOkClick(String money,String date,long timeStamp);
    }
    public OnOkClick mOnOkClick = null;

    public void setOnOkClick(OnOkClick onOkClick) {
        mOnOkClick = onOkClick;
    }

    public interface OnDateClick {
        void onDateClick(String str);
    }
    private OnDateClick mOnDateClick;

    public void setOnDateClick(OnDateClick onDateClick){
        this.mOnDateClick = onDateClick;
    }


    public void showKeyboard() {
        int visibility = ll_bookkeepingkeyboard.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            ll_bookkeepingkeyboard.setVisibility(View.VISIBLE);
        }
    }

    public boolean hideKeyboard() {
        int visibility = ll_bookkeepingkeyboard.getVisibility();
        if (visibility == View.VISIBLE) {
            ll_bookkeepingkeyboard.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private Keyboard.Key getKeyByKeyCode(int keyCode) {
        if (null != mKeyboardNumber) {
            List<Keyboard.Key> mKeys = mKeyboardNumber.getKeys();
            for (int i = 0, size = mKeys.size(); i < size; i++) {
                Keyboard.Key mKey = mKeys.get(i);

                int codes[] = mKey.codes;

                if (codes[0] == keyCode) {
                    return mKey;
                }
            }
        }

        return null;
    }

    public void setDate(Date date){
        this.mDate = date;
        if(DateUtils.isToday(date.getTime())){
            txt_date.setText("今天");
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            txt_date.setText(sdf.format(date));
        }
    }


}