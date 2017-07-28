package com.rtmap.gm.wearapplication;

import android.app.Application;

import com.lzy.okgo.OkGo;

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
        OkGo.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
