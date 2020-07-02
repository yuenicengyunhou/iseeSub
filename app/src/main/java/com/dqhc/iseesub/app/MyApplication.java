package com.dqhc.iseesub.app;

import android.app.Application;

import com.dqhc.iseesub.com.dqhc.iseesub.tools.AppUtils;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
    }
}
