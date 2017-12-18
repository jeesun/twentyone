package com.jeesun.twentyone.util;

/**
 * Created by simon on 2017/12/12.
 */

/**
 * 字体工具
 */
public class TypefaceUtil {
    private static TypefaceUtil instance = new TypefaceUtil();

    private TypefaceUtil(){

    }

    public static TypefaceUtil getInstance(){
        return instance;
    }

    public String getTypefaceUri(String name){
        if(null == name || "".equals(name)){
            return name;
        }
        if(name.contains("系统默认")){
            return null;
        }
        StringBuilder sb = new StringBuilder("fonts/");
        sb.append(name);
        if("连笔中文签名字体".equals(name)){
            sb.append(".TTF");
        }else{
            sb.append(".ttf");
        }

        return sb.toString();
    }
}
