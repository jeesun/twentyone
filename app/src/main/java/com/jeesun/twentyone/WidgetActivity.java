package com.jeesun.twentyone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jeesun.twentyone.util.AndroidFileUtils;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.PickUtil;
import com.jeesun.twentyone.widget.ChangeBgReceiver;
import com.jeesun.twentyone.widget.ChangeClockColorReceiver;
import com.jeesun.twentyone.widget.WidgetProvider;

import java.io.File;
import java.io.IOException;

public class WidgetActivity extends AppCompatActivity {
    private static final String TAG = WidgetActivity.class.getName();
    private AppCompatButton btnSwitch, btnSwitchTextColor;

    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public final static String IMAGE_TYPE = "image/*";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.desktop_widget);

        btnSwitch = findViewById(R.id.switch_widget_bg);
        btnSwitchTextColor = findViewById(R.id.switch_text_color);

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(IMAGE_TYPE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }
        });

        btnSwitchTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                int widgetColor = pref.getInt("widgetColor", -1);
                SharedPreferences.Editor editor = pref.edit();
                //0指代黑色，1指代白色
                if(0==widgetColor){
                    editor.putInt("widgetColor", 1);
                }else if(1 == widgetColor){
                    editor.putInt("widgetColor", 0);
                }else if(-1 == widgetColor){
                    editor.putInt("widgetColor", 0);
                }
                editor.apply();

                //参考http://blog.csdn.net/chenshengfa/article/details/71407704
                //android 8.0 后台限制，如果这样发送隐式广播，receiver将接收不到广播。
                //Intent intent = new Intent(WidgetProvider.ACTION_UPDATE_WIDGET_COLOR);
                Intent intent = new Intent(WidgetActivity.this, ChangeClockColorReceiver.class);
                sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                //Toast.makeText(this, "获取相册", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();
                if(null != uri){
                    Log.i(TAG, uri.toString());
                    Log.i(TAG, uri.getPath());
                    //复制文件
                    File picFile = new File(uri.getPath());
                    if(picFile.exists()){
                        copyPic(uri.getPath());
                    }else{
                        Log.i(TAG, "图片存在但uri.getPath不是标准格式");
                        if(null!=uri.toString() && !"".equals(uri.toString())){
                            String picRealPath = PickUtil.getPath(WidgetActivity.this, uri);
                            Log.i(TAG, "picRealPath=" + picRealPath);
                            if(null != picRealPath && !"".equals(picRealPath)){
                                copyPic(picRealPath);
                            }
                        }
                    }
                }
            }
        }
    }

    private void copyPic(String picRealPath) {
        try {
            if (AndroidFileUtils.fileCopy(picRealPath, ContextUtil.widgetPicDir, true)){
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("widgetPicName", AndroidFileUtils.getFileName(picRealPath));
                editor.apply();

                //参考http://blog.csdn.net/chenshengfa/article/details/71407704
                //android 8.0 后台限制，如果这样发送隐式广播，receiver将接收不到广播。
                //Intent intent = new Intent(WidgetProvider.ACTION_UPDATE_WIDGET_BG_PIC);
                Intent intent = new Intent(WidgetActivity.this, ChangeBgReceiver.class);
                sendBroadcast(intent);
            }else{
                Log.i(TAG, "图片路径存在问题");
                Toast.makeText(this, R.string.apply_bg_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
}
