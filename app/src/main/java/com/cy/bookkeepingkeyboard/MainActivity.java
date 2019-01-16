package com.cy.bookkeepingkeyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cy.bookingkeepkeyboard.KeyboardUtil;

public class MainActivity extends AppCompatActivity {

    private EditText edt2;
    private EditText edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt = findViewById(R.id.edt);
        edt2 = findViewById(R.id.edt2);
        final KeyboardUtil keyboardUtil = new KeyboardUtil(this);
        keyboardUtil.attachTo(edt);
        edt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardUtil.attachTo(edt);
                return false;
            }
        });
        edt2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardUtil.attachTo(edt2);
                return false;
            }
        });

    }
}
