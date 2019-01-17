package com.cy.bookingkeepkeyboard;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 自定义键盘
 * Created by xuejinwei on 16/3/5.
 */
public class KeyboardUtil {
    private Activity mActivity;

    private MyKeyBoardView mKeyboardView;
    private Keyboard mKeyboardNumber;//数字键盘
    private TextView mEditText;


    public KeyboardUtil(Activity activity) {
        this.mActivity = activity;
        mKeyboardNumber = new Keyboard(mActivity, R.xml.keyboard_number);
        mKeyboardView = mActivity.findViewById(R.id.keyboard_view);
    }

    /**
     * edittext绑定自定义键盘
     *
     * @param editText 需要绑定自定义键盘的edittext
     */
    public void attachTo(TextView editText) {
        this.mEditText = editText;
        hideSoftInput(mActivity);
        showSoftKeyboard();
    }

    public void showSoftKeyboard() {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = new Keyboard(mActivity, R.xml.keyboard_number);
        }
        if (mKeyboardView == null) {
            mKeyboardView = (MyKeyBoardView) mActivity.findViewById(R.id.keyboard_view);
        }

        mKeyboardView.setKeyboard(mKeyboardNumber);

        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setVisibility(View.VISIBLE);
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
            CharSequence editable = mEditText.getText();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null) {
                    if(editable.length() > 1) {
                        mEditText.setText(editable.subSequence(0, editable.length() - 1));
                    }else {
                        //如果只剩1位，按删除置0
                        mEditText.setText("0");
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {// 确定按钮,隐藏键盘
                hideKeyboard();
                if (mOnOkClick != null) {
                    mOnOkClick.onOkClick();
                }
            }else if(primaryCode == 1001){
                //今天

            }else if(primaryCode == 1002){
                //+

            }else if(primaryCode == 1003){
                //-;

            }else {
                mEditText.setText(editable+Character.toString((char) primaryCode));
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
        void onOkClick();
    }


    public OnOkClick mOnOkClick = null;

    public void setOnOkClick(OnOkClick onOkClick) {
        mOnOkClick = onOkClick;
    }


    private boolean isNumber(String str) {
        String wordstr = "0123456789";
        return wordstr.contains(str);
    }


    public void showKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    public void hideKeyboard() {
        int visibility = mKeyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            mKeyboardView.setVisibility(View.GONE);
        }
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

}