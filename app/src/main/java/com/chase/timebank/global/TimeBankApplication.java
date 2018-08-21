package com.chase.timebank.global;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.xutils.x;

/**
 * Created by chase on 2018/4/19.
 */

public class TimeBankApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//xUtils初始化
        x.Ext.setDebug(true); // 是否输出debug日志
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
