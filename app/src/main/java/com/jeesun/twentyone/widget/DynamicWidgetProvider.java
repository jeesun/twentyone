package com.jeesun.twentyone.widget;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
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
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_MULTI_PROCESS;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by simon on 2017/12/25.
 */

public class DynamicWidgetProvider extends AppWidgetProvider {

    private static final String TAG = DynamicWidgetProvider.class.getName();

    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String CLICK_ACTION = "com.simon.widget.dynamic.CLICK";
    //Android 定时器实现的几种方式和removeCallbacks失效问题详解
    //http://blog.csdn.net/xiaanming/article/details/9011193
    /*private static Runnable runnable;
    private static Handler handler;*/
    private static int currentIndex = 0;

    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<Integer> drawableIdList = new ArrayList<>();

    private static int widgetLayoutId = R.layout.dynamic_widget;

    private static Timer timer;
    private static TimerTask timerTask;
    private Context context;
    private Handler timerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0x123){
                currentIndex++;
                updateFrame(context, DynamicWidgetProvider.class);
            }
            return false;
        }
    });
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "执行onDeleted");
        super.onDeleted(context, appWidgetIds);
        //widget被从屏幕移除
        /*if (null != runnable && null != handler){
            handler.removeCallbacks(runnable);
        }*/
        if (null != timer){
            timer.cancel();
            timer = null;
        }
        if (null != timerTask){
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "执行onDisabled");
        //最后一个widget被从屏幕移除
        super.onDisabled(context);
        /*if (null != runnable && null != handler){
            handler.removeCallbacks(runnable);
        }*/
        if (null != timer){
            timer.cancel();
            timer = null;
        }
        if (null != timerTask){
            timerTask.cancel();
            timerTask = null;
        }
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
        this.context = context;

        //启动TimerService，用于更新时间
        Intent intent = new Intent();
        intent.setAction(ContextUtil.TIMER_TASK);
        intent.setPackage(context.getPackageName());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

        //initFrameBitmaps太耗时，所以把这段代码调到initFrameBitmaps之前执行，防止这段代码不生效
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        updateDate(context, rv);
        updateWidgetTextColor(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,DynamicWidgetProvider.class);
        manager.updateAppWidget(cn, rv);

        initFrameBitmaps(context);
        for (int appWidgetId : appWidgetIds){
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
        //initHandler(context, DynamicWidgetProvider.class);
        initTimer(context, DynamicWidgetProvider.class);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        //这一行是有必要的，因为onUpdate可能没有执行，context可能为null
        this.context = context;
        final String action = intent.getAction();
        Log.i(TAG, "广播"+action+"已接收");

        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        updateDate(context, rv);
        updateWidgetTextColor(context, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,DynamicWidgetProvider.class);
        manager.updateAppWidget(cn, rv);

        initFrameBitmaps(context);

        if (CLICK_ACTION.equals(action)){
            Toast.makeText(context, R.string.appwidget_dynamic, Toast.LENGTH_SHORT).show();
            //initHandler(context, DynamicWidgetProvider.class);
            initTimer(context, DynamicWidgetProvider.class);
        }
    }

    private void initFrameBitmaps(final Context context) {
        Log.i(TAG, "initFrameBitmaps");
        if (!bitmapList.isEmpty()){
            bitmapList.clear();
        }
        if (!drawableIdList.isEmpty()){
            drawableIdList.clear();
        }

        //图片长不得超过940px，否则程序卡住。
        /*drawableIdList.add(R.drawable.frame1);
        drawableIdList.add(R.drawable.frame2);
        drawableIdList.add(R.drawable.frame3);
        drawableIdList.add(R.drawable.frame4);
        drawableIdList.add(R.drawable.frame5);*/
        drawableIdList.add(R.drawable.time_umaru01);
        drawableIdList.add(R.drawable.time_umaru02);

        for (int i=0; i<drawableIdList.size(); i++){
            bitmapList.add(BitmapFactory.decodeResource(context.getResources(), drawableIdList.get(i)));
        }
    }

    private void updateWidgetTextColor(Context context, RemoteViews rv) {
        SharedPreferences pref = context.getSharedPreferences(ContextUtil.SHARED_PREF_DYNAMIC, MODE_PRIVATE);
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

    static void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "appWidgetId = " + appWidgetId);
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        Intent intentClick = new Intent(context, DynamicWidgetProvider.class);
        intentClick.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0);
        rv.setOnClickPendingIntent(R.id.background, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    //实验证明，使用【Handler、Timer、TimerTask】比单纯的用【Handler、Runnable】更稳定，定时任务停止运行的几率更低。
    /*private void initHandler(final Context context, final Class<?> c){
        Log.i(TAG, "initHandler");
        //恢复默认状态，停止正在运行的线程
        if (null != runnable && null != handler){
            handler.removeCallbacks(runnable);
        }
        if (null == handler){
            handler = new Handler();
        }
        currentIndex = 0;

        if (null == runnable){
            runnable = new Runnable() {
                @Override
                public void run() {
                    currentIndex++;
                    updateFrame(context, c);
                    handler.postDelayed(this, 500);
                }
            };
        }

        handler.postDelayed(runnable, 500);
    }*/

    private void initTimer(final Context context, final Class<?> c){
        currentIndex = 0;
        /*if (null == timerHandler){
            timerHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    if (message.what == 0x123){
                        currentIndex++;
                        updateFrame(context, c);
                    }
                    return false;
                }
            });
        }*/
        if (null == timer){
            timer = new Timer();
        }else{
            timer.cancel();
            timer = null;
            timer = new Timer();
        }
        if (null == timerTask){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    // 发送空消息，通知界面更新
                    timerHandler.sendEmptyMessage(0x123);
                }
            };
        }else{
            timerTask.cancel();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    // 发送空消息，通知界面更新
                    timerHandler.sendEmptyMessage(0x123);
                }
            };
        }
        timer.schedule(timerTask, 0, 500);
    }

    private void updateFrame(Context context, Class<?> c) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);

        rv.setImageViewBitmap(R.id.background, bitmapList.get(currentIndex%(bitmapList.size())));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,c);
        appWidgetManager.updateAppWidget(cn, rv);
    }
}