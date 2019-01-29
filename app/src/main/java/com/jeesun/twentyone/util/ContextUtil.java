package com.jeesun.twentyone.util;

import android.os.Environment;

/**
 * Created by simon on 2017/12/12.
 */

public class ContextUtil {

    public static final String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //public static final String picSavePath = dcimPath + "/Camera";

    public static final String sdPath = Environment.getExternalStorageDirectory().getPath();
    public static final String picSavePath = sdPath + "/TwentyOne";

    public static final String widgetPicDir = picSavePath + "/widget";

    public static final String fontPath = "/TwentyOne/fonts";
    public static final String fontFullPath = Environment.getExternalStorageDirectory().getPath() + fontPath;

    public static final int PIC_LOCAL = 0;
    public static final int PIC_WEB = 1;

    public static final String PICASSO_TAG_LOCAL = "local";
    public static final String PICASSO_TAG_WEB = "web";

    public static final String DYNAMIC_UPDATE_TAG = "dynamic";
    public static final String SHARED_PREF_DYNAMIC = "dynamic";

    public static final int MAX_INTERVAL_SECONDS = 60;
    public static final int TIME_INTERVAL = 45;

    public static final String TIMER_TASK = "com.simon.widget.TIMER_TASK";

    public static final String WALLPAPER_BASE_URL = "http://wallpaper.apc.360.cn";
}
