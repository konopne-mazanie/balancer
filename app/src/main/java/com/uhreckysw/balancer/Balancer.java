package com.uhreckysw.balancer;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

public class Balancer extends Application {
    private static Balancer instance;

    public static Balancer getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
