package com.jeesun.twentyone.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;

import static android.content.Context.MODE_PRIVATE;

public class ChangeClockColorReceiver extends BroadcastReceiver {
    private static final String TAG = ChangeClockColorReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        updateWidgetTextColor(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    private void updateWidgetTextColor(Context context, RemoteViews rv) {
        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        int widgetColor = pref.getInt("widgetColor", -1);
        //0指代黑色，1指代白色
        if(0==widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
            Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }else if(1 == widgetColor || -1 == widgetColor){
            //-1说明SharedPreferences还没有存这个item，那么字体是默认的黑色。
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.white));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.white));
            Toast.makeText(context, "已切换为白色", Toast.LENGTH_SHORT).show();
        }
    }
}
