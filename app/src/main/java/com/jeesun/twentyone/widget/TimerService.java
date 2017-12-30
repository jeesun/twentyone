package com.jeesun.twentyone.widget;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.util.Lauar;
import com.jeesun.twentyone.util.Lunar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by simon on 2017/12/25.
 */

public class TimerService extends Service {
    private static final String TAG = TimerService.class.getName();
    private static Timer mTimer;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TimerService", "executed at " + new Date().toString());
            }
        }).start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        //发布
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        //测试
        //calendar.add(Calendar.MINUTE, 1);

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.i(TAG, "闹钟" + df.format(calendar.getTime()));

        Intent i = new Intent(this, AlarmReceiver.class);

        //Intent i = new Intent(TimerService.this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //1000*60*60*24 一天
        //发布
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pi);

        //测试
        //manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*5, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "执行onCreate");
        super.onCreate();

        initTimer();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "执行onDestroy");
        super.onDestroy();
        if (null != mTimer){
            mTimer.cancel();
            mTimer = null;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx){

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void setBitmap(RemoteViews views, int resId, Bitmap bitmap){
        Bitmap proxy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(proxy);
        c.drawBitmap(bitmap, new Matrix(), null);
        views.setImageViewBitmap(resId, proxy);
    }

    /**
     * timer不保证精确度且在无法唤醒cpu,不适合后台任务的定时。
     */
    private void initTimer() {
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
                //定时发送广播，更新时间
                Intent updateIntent=new Intent(WidgetProvider.ACTION_UPDATE_ALL);
                sendBroadcast(updateIntent);
            }
        }, startDate.getTime(), timeInterval);
    }
}