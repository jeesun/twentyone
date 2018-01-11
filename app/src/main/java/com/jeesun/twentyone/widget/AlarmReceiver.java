package com.jeesun.twentyone.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jeesun.twentyone.WidgetActivity;

/**
 * Created by simon on 2017/12/30.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private final static String TAG = AlarmReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "执行onReceive");
        //参考http://blog.csdn.net/chenshengfa/article/details/71407704
        //android 8.0 后台限制，如果这样发送隐式广播，receiver将接收不到广播。
        /*Intent updateWidgetBgIntent = new Intent(WidgetProvider.ACTION_UPDATE_ALL);
        context.sendBroadcast(updateWidgetBgIntent);
        Log.i(TAG, "广播" + WidgetProvider.ACTION_UPDATE_ALL + "已发送");*/
        Intent updateClockIntent = new Intent(context, ChangeClockColorReceiver.class);
        context.sendBroadcast(updateClockIntent);
    }
}
