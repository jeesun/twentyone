package com.jeesun.twentyone.widget;

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
    private Timer mTimer;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "执行onCreate");
        super.onCreate();

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
                updata();
            }
        }, startDate.getTime(), timeInterval);
    }

    private void updata() {
        //String time = sdf.format(new Date());
        Date date = new Date();
        //Log.i(TAG, date.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Lunar lunar = new Lunar(cal);
        String time = Lauar.getWeekOfDate(cal) + " " + lunar.toString();

        //Log.i(TAG, time);
        String monthAndDay = Lauar.getChinaMonthAndDay(new Date());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.widget);
        rv.setTextViewText(R.id.month_day, monthAndDay);
        rv.setTextViewText(R.id.time, time);


        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int widgetColor = pref.getInt("widgetColor", -1);
        //0指代黑色，1指代白色
        if(0==widgetColor){
            rv.setTextColor(R.id.month_day, getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, getResources().getColor(R.color.black));
            //Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }else if(1 == widgetColor){
            rv.setTextColor(R.id.month_day, getResources().getColor(R.color.white));
            rv.setTextColor(R.id.time, getResources().getColor(R.color.white));
            //Toast.makeText(context, "已切换为白色", Toast.LENGTH_SHORT).show();
        }else if(-1 == widgetColor){
            rv.setTextColor(R.id.month_day, getResources().getColor(R.color.black));
            rv.setTextColor(R.id.time, getResources().getColor(R.color.black));
            //Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
        }

        AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn =new ComponentName(getApplicationContext(),WidgetProvider.class);
        manager.updateAppWidget(cn, rv);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
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
}