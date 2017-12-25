package com.jeesun.twentyone;

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

import com.jeesun.twentyone.widget.WidgetProvider;

public class WidgetActivity extends AppCompatActivity {
    private static final String TAG = WidgetActivity.class.getName();
    private AppCompatButton btnSwitch, btnSwitchTextColor;

    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
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

                Intent intent = new Intent(WidgetProvider.ACTION_UPDATE_WIDGET_COLOR);
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
                    Intent intent = new Intent(WidgetProvider.ACTION_UPDATE_ALL);
                    Log.i(TAG, uri.toString());
                    Log.i(TAG, uri.getPath());
                    intent.putExtra("uriString", uri.toString());
                    intent.putExtra("widgetBgPicPath", uri.getPath());
                    sendBroadcast(intent);
                    Log.i(TAG, "广播已发送");
                }
            }
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
