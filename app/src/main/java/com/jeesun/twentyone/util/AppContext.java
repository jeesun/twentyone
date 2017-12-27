package com.jeesun.twentyone.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by simon on 2017/12/28.
 */

public class AppContext extends Application {
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
    }

    public static Context getContext()
    {
        return instance;
    }
}
