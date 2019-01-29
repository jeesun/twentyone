package com.jeesun.twentyone.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.service.TimerService;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.Lauar;
import com.jeesun.twentyone.util.Lunar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2017/12/25.
 */

public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getName();
    // 启动TimerService服务所对应的action
    public final Intent TIMER_TASK =
            new Intent("com.simon.widget.TIMER_TASK");
    // 更新 widget 的广播对应的action
    //public static final String ACTION_UPDATE_ALL = "com.simon.widget.UPDATE_ALL";
    //public final static String ACTION_UPDATE_WIDGET_BG_PIC = "com.simon.widget.UPDATE_WIDGET_BG_PIC";
    //public final static String ACTION_UPDATE_WIDGET_COLOR = "com.simon.widget.UPDATE_WIDGET_COLOR";
    //public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

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
        Intent downloadIntent = new Intent(context, TimerService.class);
        context.stopService(downloadIntent);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(final Context context) {
        Log.i(TAG, "执行onEnabled");
        //widget添加到屏幕上执行
        super.onEnabled(context);
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "执行onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //启动TimerService，用于更新时间
        Intent intent = new Intent();
        intent.setAction(ContextUtil.TIMER_TASK);
        intent.setPackage(context.getPackageName());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        /*Intent downloadIntent = new Intent(context, TimerService.class);
        context.startService(downloadIntent);*/

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        updateWidget(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.i(TAG, "广播"+action+"已接收");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        updateWidget(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    private void updateWidget(Context context, RemoteViews rv){
        updateWidgetBgPic(context,rv);
        updateDate(context, rv);
        updateWidgetTextColor(context, rv);
    }

    private void updateWidgetTextColor(Context context, RemoteViews rv) {
        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        int widgetColor = pref.getInt("widgetColor", -1);
        //0指代黑色，1指代白色
        if(0==widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
            //Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }else if(1 == widgetColor || -1 == widgetColor){
            //-1说明SharedPreferences还没有存这个item，那么字体是默认的黑色。
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.white));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.white));
            //Toast.makeText(context, "已切换为白色", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWidgetBgPic(Context context, RemoteViews rv) {
        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        String widgetPicName = pref.getString("widgetPicName", null);
        if (null == widgetPicName || "".equals(widgetPicName)){
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
        }
    }

    private void updateDate(Context context, RemoteViews rv) {
        //Log.i(TAG, date.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Lunar lunar = new Lunar(cal);
        String time = Lauar.getWeekOfDate(cal) + " " + lunar.toString();
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Log.i(TAG, time);
        String monthAndDay = Lauar.getChinaMonthAndDay(new Date());

        rv.setTextViewText(R.id.month_day, monthAndDay);
        rv.setTextViewText(R.id.time, time);


        SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
        int widgetColor = pref.getInt("widgetColor", -1);
        //0指代黑色，1指代白色
        if(0==widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
        }else if(1 == widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.white));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.white));
        }else if(-1 == widgetColor){
            rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
        }
    }
}