package com.jeesun.twentyone.widget;

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
    // 启动ExampleAppWidgetService服务对应的action
    public final Intent EXAMPLE_SERVICE_INTENT =
            new Intent("android.appwidget.action.EXAMPLE_APP_WIDGET_SERVICE");
    // 更新 widget 的广播对应的action
    public final static String ACTION_UPDATE_WIDGET_BG_PIC = "com.simon.widget.UPDATE_WIDGET_BG_PIC";
    public final static String ACTION_UPDATE_WIDGET_COLOR = "com.simon.widget.UPDATE_WIDGET_COLOR";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    // 按钮信息
    private static final int BUTTON_SHOW = 1;

    private Timer mTimer;
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "执行onDeleted");
        super.onDeleted(context, appWidgetIds);
        //widget被从屏幕移除
        if (null != mTimer){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "执行onDisabled");
        //最后一个widget被从屏幕移除
        Intent downloadIntent = new Intent(EXAMPLE_SERVICE_INTENT);
        downloadIntent.setPackage(context.getPackageName());
        context.stopService(downloadIntent);
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "执行onEnabled");
        //widget添加到屏幕上执行
        Intent downloadIntent = new Intent(EXAMPLE_SERVICE_INTENT);
        downloadIntent.setPackage(context.getPackageName());
        context.startService(downloadIntent);
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
        if(ACTION_UPDATE_WIDGET_BG_PIC.equals(action)){
            updateWidgetBgPic(context);
        }else if(ACTION_UPDATE_WIDGET_COLOR.equals(action)){
            updata(context);
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
        }else if(ACTION_BOOT_COMPLETED.equals(action)){
            Log.i(TAG, "执行定时任务");
            // 时间类
            Calendar startDate = Calendar.getInstance();

            //设置开始执行的时间为 某年-某月-某月 00:00:00
            startDate.set(
                    startDate.get(Calendar.YEAR),
                    startDate.get(Calendar.MONTH),
                    startDate.get(Calendar.DATE),
                    0, 0, 0);

            // 1天的毫秒设定
            long timeInterval = 60 * 60 * 1000 * 24;

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateWidgetBgPic(context);
                    updata(context);
                }
            }, startDate.getTime(), timeInterval);
        }else if("android.appwidget.action.APPWIDGET_UPDATE".equals(action)){
            //说明应用被更新或者widget被用户创建
            updateWidgetBgPic(context);
            updata(context);
        }
    }

    private void updateWidgetBgPic(Context context) {
        File picFile = new File(ContextUtil.widgetPicPath);
        if(picFile.exists()){
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

            Uri uri = Uri.fromFile(picFile);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);
                bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);
                rv.setImageViewBitmap(R.id.background, bitmap);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn =new ComponentName(context,WidgetProvider.class);
                manager.updateAppWidget(cn, rv);
                Toast.makeText(context, "桌面部件背景图已更新", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "图片不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void updata(Context context) {
        //String time = sdf.format(new Date());
        Date date = new Date();
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