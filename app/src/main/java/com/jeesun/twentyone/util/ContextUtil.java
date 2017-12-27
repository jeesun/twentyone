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
    public static final String widgetPicPath = widgetPicDir + "/widget_bg.png";

    public static final String fontPath = "/TwentyOne/fonts";
    public static final String fontFullPath = Environment.getExternalStorageDirectory().getPath() + fontPath;

    public static final int PIC_LOCAL = 0;
    public static final int PIC_WEB = 1;

    public static final String PICASSO_TAG_LOCAL = "local";
    public static final String PICASSO_TAG_WEB = "web";
}
