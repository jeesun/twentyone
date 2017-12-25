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
import android.widget.RemoteViews;

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

    @Override
    public void onCreate() {
        super.onCreate();

        // 时间类
        Calendar startDate = Calendar.getInstance();

        //设置开始执行的时间为 某年-某月-某月 00:00:00
        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

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


        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_card);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);

        bitmap = getRoundedCornerBitmap(bitmap,6);

        rv.setImageViewBitmap(R.id.background, bitmap);*/
        //rv.setImageViewResource(R.id.background, R.drawable.new_card);
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