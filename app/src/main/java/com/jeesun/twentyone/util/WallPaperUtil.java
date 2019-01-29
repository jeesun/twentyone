package com.jeesun.twentyone.util;

/**
 * 360壁纸处理
 */
public class WallPaperUtil {
    public static final String DEFAULT_BDR = "520_270_80";

    /**
     * 返回指定大小和质量的图片
     * @param url
     * @param bdr
     * @return
     */
    public static String assignSize(String url, String bdr){
        return url.substring(0, url.lastIndexOf("/")) + "/bdr/" + bdr + url.substring(url.lastIndexOf("/"));
    }
}
