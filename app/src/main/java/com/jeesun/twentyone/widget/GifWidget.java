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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class GifWidget extends AppWidgetProvider {
    private static final String TAG = GifWidget.class.getName();

    public static final String CLICK_ACTION = "com.simon.widget.gif.CLICK";
    //Android 定时器实现的几种方式和removeCallbacks失效问题详解
    //http://blog.csdn.net/xiaanming/article/details/9011193
    private static Runnable runnable;
    private static Handler handler;
    private int currentIndex = 0;

    private List<Bitmap> bitmapList = new ArrayList<>();
    private List<Integer> drawableIdList = new ArrayList<>();

    private static int widgetLayoutId = R.layout.gif_widget;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }

        initHandler(context, GifWidget.class);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (null != runnable && null != handler){
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        if (null != runnable && null != handler){
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.i(TAG, "广播"+action+"已接收");

        initFrameBitmaps(context);
        if (CLICK_ACTION.equals(action)){
            Toast.makeText(context, R.string.appwidget_dynamic, Toast.LENGTH_SHORT).show();
            initHandler(context, GifWidget.class);
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
        drawableIdList.add(R.drawable.umaru01);
        drawableIdList.add(R.drawable.umaru02);

        for (int i=0; i<drawableIdList.size(); i++){
            bitmapList.add(BitmapFactory.decodeResource(context.getResources(), drawableIdList.get(i)));
        }
    }

    static void onWidgetUpdate(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i(TAG, "appWidgetId = " + appWidgetId);
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        Intent intentClick = new Intent(context, GifWidget.class);
        intentClick.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0);
        rv.setOnClickPendingIntent(R.id.background, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private void initHandler(final Context context, final Class<?> c){
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
    }

    private void updateFrame(Context context, Class<?> c) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);

        rv.setImageViewBitmap(R.id.background, bitmapList.get(currentIndex%(bitmapList.size())));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName cn =new ComponentName(context,c);
        appWidgetManager.updateAppWidget(cn, rv);
    }
}

