package com.cy.bkbdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cy.bookkeepingkeyboard.BookKeyboardHelper;

import org.feezu.liuli.timeselector.TimeSelector;
import org.feezu.liuli.timeselector.Utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private BookKeyboardHelper bookKeyboardHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView edt2 = findViewById(R.id.edt2);
        bookKeyboardHelper = new BookKeyboardHelper(this);
        bookKeyboardHelper.setOnDateClick(new BookKeyboardHelper.OnDateClick() {
            @Override
            public void onDateClick(String str) {
                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
                String chooseDate = inFormat.format(new Date());


                TimeSelector timeSelector = new TimeSelector(MainActivity.this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        Date date = DateUtil.parse(time,"yyyy-MM-dd HH:mm");
                        bookKeyboardHelper.setDate(date);

                    }
                }, "2017-06-08 00:00", "2125-12-12 00:00");
                timeSelector.setMode(TimeSelector.MODE.YMD);
                timeSelector.setTitle("选择日期");
                timeSelector.show();
            }
        });
        bookKeyboardHelper.setOnOkClick(new BookKeyboardHelper.OnOkClick() {
            @Override
            public void onOkClick(String money,String date,long timeStamp) {
                bookKeyboardHelper.hideKeyboard();
            }
        });

        edt2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bookKeyboardHelper.attachTo(edt2);
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(!bookKeyboardHelper.hideKeyboard()) {
            super.onBackPressed();
        }

    }
}
