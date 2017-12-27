package com.jeesun.twentyone.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.util.PickUtil;

import java.io.File;
import java.io.IOException;

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
    public final static String ACTION_UPDATE_ALL = "com.simon.widget.UPDATE_ALL";
    public final static String ACTION_UPDATE_WIDGET_COLOR = "com.simon.widget.UPDATE_WIDGET_COLOR";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    // 按钮信息
    private static final int BUTTON_SHOW = 1;

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //widget被从屏幕移除
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //最后一个widget被从屏幕移除
        context.stopService(new Intent(context,TimerService.class));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        //widget添加到屏幕上执行
        context.startService(new Intent(context,TimerService.class));
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        //刷新的时候执行widget
        //remoteView  AppWidgetManager
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.i(TAG, "广播已接收");
        Log.i(TAG, "OnReceive:Action: " + action);
        if(ACTION_UPDATE_ALL.equals(action)){
            //String uriString = intent.getStringExtra("uriString");
            //String widgetBgPicPath = intent.getStringExtra("widgetBgPicPath");
            SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
            String uriString = pref.getString("uriString", null);
            String widgetBgPicPath = pref.getString("widgetBgPicPath", null);
            Log.i(TAG, "uriString=" + uriString);
            Log.i(TAG, "widgetBgPicPath=" + widgetBgPicPath);

            if(null != widgetBgPicPath && !"".equals(widgetBgPicPath)){
                Log.i(TAG, "widgetBgPicPath="+widgetBgPicPath);
                //"更新"广播
                updateAllAppWidget(context, uriString, widgetBgPicPath);
            }else{
                //显示默认的图片
            }
            //updateAllAppWidget(context, AppWidgetManager.getInstance(context), widgetBgPic);
        }else if(ACTION_UPDATE_WIDGET_COLOR.equals(action)){
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
            SharedPreferences pref = context.getSharedPreferences("data", MODE_PRIVATE);
            int widgetColor = pref.getInt("widgetColor", -1);
            //0指代黑色，1指代白色
            if(0==widgetColor){
                rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
                rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
                Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
            }else if(1 == widgetColor){
                rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.white));
                rv.setTextColor(R.id.time, context.getResources().getColor(R.color.white));
                Toast.makeText(context, "已切换为白色", Toast.LENGTH_SHORT).show();
            }else if(-1 == widgetColor){
                rv.setTextColor(R.id.month_day, context.getResources().getColor(R.color.black));
                rv.setTextColor(R.id.time, context.getResources().getColor(R.color.black));
                Toast.makeText(context, "已切换为黑色", Toast.LENGTH_SHORT).show();
            }
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName cn =new ComponentName(context,WidgetProvider.class);
            manager.updateAppWidget(cn, rv);
        }else if(ACTION_BOOT_COMPLETED.equals(action)){

        }
    }

    private void updateAllAppWidget(Context context, String uriString, String widgetBgPicPath) {
        Log.i(TAG, "更新广播");
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);

        File picFile = new File(widgetBgPicPath);
        if(picFile.exists()){
            Uri uri = Uri.fromFile(picFile);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);

                bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);

                rv.setImageViewBitmap(R.id.background, bitmap);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn =new ComponentName(context,WidgetProvider.class);
                manager.updateAppWidget(cn, rv);
                Toast.makeText(context, "桌面部件背景图已更新", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.i(TAG, "图片未找到");
            if(null!=uriString && !"".equals(uriString)){
                Uri uri = Uri.parse(uriString);
                String picRealPath = PickUtil.getPath(context, uri);
                Log.i(TAG, "picRealPath=" + picRealPath);
                Bitmap bitmap = BitmapFactory.decodeFile(picRealPath);
                Log.i(TAG, bitmap.getHeight()+"x"+bitmap.getWidth());
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                bitmap = Bitmap.createScaledBitmap(bitmap, 800, 400, true);

                bitmap = TimerService.getRoundedCornerBitmap(bitmap,6);

                rv.setImageViewBitmap(R.id.background, bitmap);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName cn =new ComponentName(context,WidgetProvider.class);
                manager.updateAppWidget(cn, rv);
                Toast.makeText(context, "桌面部件背景图已更新", Toast.LENGTH_SHORT).show();
            }

        }
    }
}