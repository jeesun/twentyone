package com.jeesun.twentyone.util;

import android.os.Environment;

/**
 * Created by simon on 2017/12/12.
 */

public class ContextUtil {

    public static final String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    public static final String picSavePath = dcimPath + "/Camera";

    public static final String fontPath = Environment.getExternalStorageDirectory().getPath() + "/TwentyOne/Fonts";
}
