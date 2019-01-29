package com.jeesun.twentyone.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.squareup.picasso.Picasso;

/**
 * Created by simon on 2017/12/28.
 */

public class AppContext extends Application {
    private static final String TAG = AppContext.class.getName();
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
        Picasso.setSingletonInstance(new Picasso.Builder(this).build());
    }

    public static Context getContext()
    {
        return instance;
    }
}
