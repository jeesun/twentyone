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
        /*Intent service = new Intent(context, TimerService.class);
        context.startService(service);*/

        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        String widgetBgPicPath = pref.getString("widgetBgPicPath", null);
        if(null != widgetBgPicPath){
            Intent updateWidgetBgIntent = new Intent(WidgetProvider.ACTION_UPDATE_WIDGET_BG_PIC);
            updateWidgetBgIntent.putExtra("widgetBgPicPath", widgetBgPicPath);
            context.sendBroadcast(updateWidgetBgIntent);
            Log.i(TAG, "广播已发送");
        }

    }
}
