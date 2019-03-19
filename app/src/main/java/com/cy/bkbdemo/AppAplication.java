package com.cy.bkbdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ychen on 2019/3/18.
 */
public class AppAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
