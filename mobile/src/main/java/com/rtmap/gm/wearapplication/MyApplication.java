package com.rtmap.gm.wearapplication;

import android.app.Application;

/**
 * Created by yxy
 * on 2017/7/21.
 */

public class MyApplication extends Application {
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
