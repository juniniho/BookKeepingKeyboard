package com.cy.bkbdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cy.bookkeepingkeyboard.BookKeyboardHelper;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView edt2 = findViewById(R.id.edt2);
        final BookKeyboardHelper bookKeyboardHelper = new BookKeyboardHelper(this);

        edt2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bookKeyboardHelper.attachTo(edt2);
                return false;
            }
        });


    }
}
