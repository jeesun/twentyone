package com.jeesun.twentyone.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by simon on 2018/1/7.
 */

public class ServiceUtils {

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (null == serviceName || ("").equals(serviceName))
            return false;
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) manager
                .getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (serviceName.equals(runningService.get(i).service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
