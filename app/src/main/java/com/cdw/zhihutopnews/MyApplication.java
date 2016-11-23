package com.cdw.zhihutopnews;

import android.app.Application;

/**
 * Created by CDW on 2016/11/5.
 */

public class MyApplication extends Application {
    public static MyApplication myApplication;

    public static Application getContext() {

        return myApplication;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

    }
}
