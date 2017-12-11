package com.jeesun.twentyone.util;

import android.graphics.Color;

/**
 * Created by simon on 2017/12/10.
 */

public class ColorUtil {
    private static ColorUtil instance = new ColorUtil();

    private ColorUtil(){

    }

    public static ColorUtil getInstance(){
        return instance;
    }

    public int getColorCode(String colorName){
        if("字体颜色（默认黑色）".equals(colorName)){
            return Color.BLACK;
        }else if("黑色".equals(colorName)){
            return Color.BLACK;
        }else if("白色".equals(colorName)){
            return Color.WHITE;
        }else if("灰色".equals(colorName)){
            return Color.GRAY;
        }else if("深灰".equals(colorName)){
            return Color.DKGRAY;
        }else if("亮灰".equals(colorName)){
            return Color.LTGRAY;
        }else if("红色".equals(colorName)){
            return Color.RED;
        }else if("黄色".equals(colorName)){
            return Color.YELLOW;
        }else if("绿色".equals(colorName)){
            return Color.GREEN;
        }else if("青色".equals(colorName)){
            return Color.CYAN;
        }else if("蓝色".equals(colorName)){
            return Color.BLUE;
        }else if("紫红".equals(colorName)){
            return Color.MAGENTA;//品红，紫红
        }
        return Color.BLACK;
    }
}
