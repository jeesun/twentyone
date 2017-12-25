package com.jeesun.twentyone.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import com.jeesun.twentyone.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by simon on 2017/12/25.
 */

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = WidgetProvider.class.getName();
    private boolean DEBUG = false;
    // 启动ExampleAppWidgetService服务对应的action
    public final Intent EXAMPLE_SERVICE_INTENT =
            new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    // 更新 widget 的广播对应的action
    public final static String ACTION_UPDATE_ALL = "com.simon.widget.UPDATE_ALL";
    // 按钮信息
    private static final int BUTTON_SHOW = 1;

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //widget被从屏幕移除
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //最后一个widget被从屏幕移除
        context.stopService(new Intent(context,TimerService.class));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //widget添加到屏幕上执行
        context.startService(new Intent(context,TimerService.class));
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //刷新的时候执行widget
        //remoteView  AppWidgetManager
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.i(TAG, "广播已接收");
        Log.i(TAG, "OnReceive:Action: " + action);
        if(ACTION_UPDATE_ALL.equals(action)){
            String widgetBgPicPath = intent.getStringExtra("widgetBgPicPath");
            if(null!=widgetBgPicPath && !"".equals(widgetBgPicPath)){
                Log.i(TAG, "widgetBgPicPath="+widgetBgPicPath);
                //"更新"广播
                updateAllAppWidget(context, widgetBgPicPath);
            }
            //updateAllAppWidget(context, AppWidgetManager.getInstance(context), widgetBgPic);
        }
    }

    private void updateAllAppWidget(Context context, String widgetBgPicPath) {
        Log.i(TAG, "更新广播");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        File picFile = new File(widgetBgPicPath);
        if(picFile.exists()){
            Uri uri = Uri.fromFile(picFile);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);

                bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);

                rv.setImageViewBitmap(R.id.background, bitmap);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn =new ComponentName(context,WidgetProvider.class);
                manager.updateAppWidget(cn, rv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "图片未找到");
        }
    }
}