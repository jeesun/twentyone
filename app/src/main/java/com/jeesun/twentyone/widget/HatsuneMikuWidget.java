package com.jeesun.twentyone.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of App Widget functionality.
 */
public class HatsuneMikuWidget extends AppWidgetProvider {

    private static final String TAG = HatsuneMikuWidget.class.getName();

    public static final String CLICK_ACTION = "com.simon.widget.hatsune_miku.CLICK";

    private int currentIndex = 0;

    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<Integer> drawableIdList = new ArrayList<>();

    private static int widgetLayoutId = R.layout.hatsune_miku_widget;

    private static Timer timer;
    private static TimerTask timerTask;
    private Context context;
    private Handler timerHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0x123){
                currentIndex++;
                updateFrame(context, HatsuneMikuWidget.class);
            }
            return false;
        }
    });
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        initTimer();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
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
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;
        final String action = intent.getAction();
        Log.i(TAG, "广播"+action+"已接收");

        initFrameBitmaps(context);
        if (CLICK_ACTION.equals(action)){
            Toast.makeText(context, R.string.appwidget_dynamic, Toast.LENGTH_SHORT).show();
            initTimer();
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
        drawableIdList.add(R.drawable.hatsune_miku01);
        drawableIdList.add(R.drawable.hatsune_miku02);
        drawableIdList.add(R.drawable.hatsune_miku03);
        drawableIdList.add(R.drawable.hatsune_miku04);
        drawableIdList.add(R.drawable.hatsune_miku05);
        drawableIdList.add(R.drawable.hatsune_miku06);
        drawableIdList.add(R.drawable.hatsune_miku07);
        drawableIdList.add(R.drawable.hatsune_miku08);
        drawableIdList.add(R.drawable.hatsune_miku09);
        drawableIdList.add(R.drawable.hatsune_miku10);
        drawableIdList.add(R.drawable.hatsune_miku11);
        drawableIdList.add(R.drawable.hatsune_miku12);
        drawableIdList.add(R.drawable.hatsune_miku13);
        drawableIdList.add(R.drawable.hatsune_miku14);
        drawableIdList.add(R.drawable.hatsune_miku15);

        for (int i=0; i<drawableIdList.size(); i++){
            bitmapList.add(BitmapFactory.decodeResource(context.getResources(), drawableIdList.get(i)));
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "appWidgetId = " + appWidgetId);
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        Intent intentClick = new Intent(context, HatsuneMikuWidget.class);
        intentClick.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0);
        rv.setOnClickPendingIntent(R.id.background, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private void updateFrame(Context context, Class<?> c) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);

        rv.setImageViewBitmap(R.id.background, bitmapList.get(currentIndex%(bitmapList.size())));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,c);
        appWidgetManager.updateAppWidget(cn, rv);
    }

    private void initTimer(){
        currentIndex = 0;

        if (null != timer){
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        if (null != timerTask){
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 发送空消息，通知界面更新
                timerHandler.sendEmptyMessage(0x123);
            }
        };
        timer.schedule(timerTask, 0, 500);
    }
}

