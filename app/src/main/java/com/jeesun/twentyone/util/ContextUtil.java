package com.jeesun.twentyone.util;

import android.os.Environment;

/**
 * Created by simon on 2017/12/12.
 */

public class ContextUtil {

    public static final String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    public static final String picSavePath = dcimPath + "/Camera";

    public static final String fontPath = "/TwentyOne/fonts";
    public static final String fontFullPath = Environment.getExternalStorageDirectory().getPath() + fontPath;

    public static final int PIC_LOCAL = 0;
    public static final int PIC_WEB = 1;
}
