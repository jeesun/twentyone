package com.jeesun.twentyone.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2017/12/27.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "开机自启服务已启动");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // Set the alarm here.
            Intent service = new Intent(context, TimerService.class);
            context.startService(service);
        }
    }
}
