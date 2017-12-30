package com.jeesun.twentyone.widget;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.util.AppContext;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.Lauar;
import com.jeesun.twentyone.util.Lunar;
import com.jeesun.twentyone.util.PickUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2017/12/25.
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getName();
    private boolean DEBUG = false;
    // 启动TimerService服务所对应的action
    public final Intent TIMER_TASK =
            new Intent("com.simon.widget.TIMER_TASK");
    // 更新 widget 的广播对应的action
    public static final String ACTION_UPDATE_ALL = "com.simon.widget.UPDATE_ALL";
    public final static String ACTION_UPDATE_WIDGET_BG_PIC = "com.simon.widget.UPDATE_WIDGET_BG_PIC";
    public final static String ACTION_UPDATE_WIDGET_COLOR = "com.simon.widget.UPDATE_WIDGET_COLOR";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "执行onDeleted");
        super.onDeleted(context, appWidgetIds);
        //widget被从屏幕移除
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "执行onDisabled");
        //最后一个widget被从屏幕移除
        Intent downloadIntent = new Intent(TIMER_TASK);
        downloadIntent.setPackage(context.getPackageName());
        context.stopService(downloadIntent);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(final Context context) {
        Log.i(TAG, "执行onEnabled");
        //widget添加到屏幕上执行
        Intent downloadIntent = new Intent(TIMER_TASK);
        downloadIntent.setPackage(context.getPackageName());
        context.startService(downloadIntent);

        updateWidgetBgPic(context);
        updateDate(context);

        super.onEnabled(context);
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "执行onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //刷新的时候执行widget
        //remoteView  AppWidgetManager
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.i(TAG, "广播"+action+"已接收");
        Log.i(TAG, "OnReceive:Action: " + action);
        if(ACTION_UPDATE_ALL.equals(action)){
            updateWidgetBgPic(context);
            updateDate(context);
            updateWidgetTextColor(context);
        }else if(ACTION_UPDATE_WIDGET_BG_PIC.equals(action)){
            updateWidgetBgPic(context);
        }else if(ACTION_UPDATE_WIDGET_COLOR.equals(action)){
            updateWidgetTextColor(context);
        }else if(ACTION_BOOT_COMPLETED.equals(action)){
            updateWidgetBgPic(context);
            updateDate(context);
            updateWidgetTextColor(context);
        }else if("android.appwidget.action.APPWIDGET_UPDATE".equals(action)){
            /**
             * ACTION_APPWIDGET_UPDATE Action faire when:
             1. an new instance of Your AppWidget added to Home Screen from AppWidget Chooser( from AppWidget provider),
             2. when requested update interval having lapsed which you have provided in AppWidget meta-data file using android:updatePeriodMillis attribute , and
             3. when device reboot
             */
            Intent downloadIntent = new Intent(TIMER_TASK);
            downloadIntent.setPackage(context.getPackageName());
            context.startService(downloadIntent);
            //说明应用被更新或者widget被用户创建
            updateWidgetBgPic(context);
            updateDate(context);
        }
    }

    private void updateWidgetTextColor(Context context) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
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
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    private void updateWidgetBgPic(Context context) {
        File picFile = new File(ContextUtil.widgetPicPath);
        if(picFile.exists()){
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

            Bitmap bitmap = BitmapFactory.decodeFile(ContextUtil.widgetPicPath);
            bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);
            bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);
            rv.setImageViewBitmap(R.id.background, bitmap);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName cn =new ComponentName(context,WidgetProvider.class);
            manager.updateAppWidget(cn, rv);
            Log.i(TAG, "桌面部件背景图已更新");
        }else{
            Log.i(TAG, "图片不存在");
        }
    }

    private void updateDate(Context context) {
        //Log.i(TAG, date.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Lunar lunar = new Lunar(cal);
        String time = Lauar.getWeekOfDate(cal) + " " + lunar.toString();

        //Log.i(TAG, time);
        String monthAndDay = Lauar.getChinaMonthAndDay(new Date());
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        rv.setTextViewText(R.id.month_day, monthAndDay);
        rv.setTextViewText(R.id.time, time);


        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        int widgetColor = pref.getInt("widgetColor", -1);
        //0指代黑色，1指代白色
        if(0==widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
            //Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }else if(1 == widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.white));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.white));
            //Toast.makeText(context, "已切换为白色", Toast.LENGTH_SHORT).show();
        }else if(-1 == widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
            //Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }
}