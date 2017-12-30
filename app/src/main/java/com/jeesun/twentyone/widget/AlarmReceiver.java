package com.jeesun.twentyone.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by simon on 2017/12/30.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = AlarmReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "AlarmReceiver执行onReceive");
        Intent updateWidgetBgIntent = new Intent(WidgetProvider.ACTION_UPDATE_ALL);
        context.sendBroadcast(updateWidgetBgIntent);
        Log.i(TAG, "广播" + WidgetProvider.ACTION_UPDATE_ALL + "已发送");
    }
}
