package com.jeesun.twentyone.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.jeesun.twentyone.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by simon on 2018/1/6.
 */

public class DynamicTimerService extends Service {
    private static final String TAG = DynamicTimerService.class.getName();
    private static final int WHAT_UPDATE_PIC = 1;
    private static int count = 0;
    private Timer mTimer;
    private TimerTask timerTask;
    private Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * onStartCommand: 在执行了startService方法之后，有可能会调用Service的onCreate方法，
     * 在这之后一定会执行Service的onStartCommand回调方法。
     * 也就是说，如果多次执行了Context的startService方法，那么Service的onStartCommand方法也会相应的多次调用。
     * onStartCommand方法很重要，我们在该方法中根据传入的Intent参数进行实际的操作，比如会在此处创建一个线程用于下载数据或播放音乐等。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "执行onStartCommand");
        updateWidget();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "执行onCreate");
        super.onCreate();
        mTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = WHAT_UPDATE_PIC;
                handler.sendMessage(message);
            }
        };

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (WHAT_UPDATE_PIC == message.what){
                    count++;
                    updateWidget();
                }
                return false;
            }
        });

        mTimer.schedule(timerTask, 0, 200);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "执行onDestroy");
        if (null != mTimer){
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }

    private void updateWidget() {
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.dynamic_widget);
        updateWidgetBgPic(this, rv);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName cn =new ComponentName(this,DynamicWidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    private void updateWidgetBgPic(Context context, RemoteViews rv) {
        Bitmap bitmap = null;
        switch (count%5){
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frame1);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frame2);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frame3);
                break;
            case 4:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frame4);
                break;
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frame5);
                break;
        }

        if (null == bitmap){
            return;
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);
        bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);
        rv.setImageViewBitmap(R.id.background, bitmap);
    }
}
