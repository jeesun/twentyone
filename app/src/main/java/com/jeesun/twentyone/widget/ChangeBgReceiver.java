package com.jeesun.twentyone.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.util.ContextUtil;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2018/1/11.
 */

public class ChangeBgReceiver extends BroadcastReceiver {
    private static final String TAG = ChangeBgReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        updateWidgetBgPic(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
        Toast.makeText(context, R.string.apply_bg_succeed, Toast.LENGTH_SHORT).show();
    }

    private void updateWidgetBgPic(Context context, RemoteViews rv) {
        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        String widgetPicName = pref.getString("widgetPicName", null);
        if (null == widgetPicName || "".equals(widgetPicName)){
            Toast.makeText(context, R.string.apply_bg_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, widgetPicName);
        File picFile = new File(ContextUtil.widgetPicDir + "/" + widgetPicName);
        if(picFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(ContextUtil.widgetPicDir + "/" + widgetPicName);
            if (null == bitmap){
                return;
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);
            bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);
            rv.setImageViewBitmap(R.id.background, null);
            rv.setImageViewBitmap(R.id.background, bitmap);

        }else{
            Log.i(TAG, "图片不存在");
            Toast.makeText(context, R.string.apply_bg_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
