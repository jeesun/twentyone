package com.jeesun.twentyone.util;

/**
 * Created by simon on 2017/12/12.
 */

public class TypefaceUtil {
    private static TypefaceUtil instance = new TypefaceUtil();

    private TypefaceUtil(){

    }

    public static TypefaceUtil getInstance(){
        return instance;
    }

    public String getTypefaceUri(String name){
        StringBuilder sb = new StringBuilder("fonts/");
        if("字体（默认方正魏碑简体）".equals(name)){
            sb.append("方正魏碑简体");
        }else{
            sb.append(name);
        }
        if("连笔中文签名字体".equals(name)){
            sb.append(".TTF");
        }else{
            sb.append(".ttf");
        }

        return sb.toString();
    }
}
