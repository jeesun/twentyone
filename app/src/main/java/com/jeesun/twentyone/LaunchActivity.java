package com.jeesun.twentyone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jeesun.twentyone.util.ContextUtil;

import java.io.File;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = LaunchActivity.class.getName();
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    String dirPath = ContextUtil.picSavePath;

    private TextView tvLaunchInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /*getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        if (existSDCard()){
            Log.i(TAG, "sd卡存在");
        }else{
            Log.i(TAG, "sd卡不存在");
        }

        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "当前android版本"+Build.VERSION.SDK_INT+"大于23");
            // 检查该权限是否已经获取
            if (ContextCompat.checkSelfPermission(LaunchActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //权限未获取
                if (!ActivityCompat.shouldShowRequestPermissionRationale(LaunchActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //第一次全新进入时，shouldShowRequestPermissionRationale方法将返回false,这里将会执行。
                    //请求权限时如果点了拒绝但是没勾选不再提醒，shouldShowRequestPermissionRationale方法将返回true，这里将不执行。
                    //点了拒绝且勾选了不再提醒，再次进入时，shouldShowRequestPermissionRationale方法也将返回false,并且权限请求将无任何响应，然后可以在下面方法中做些处理，提示用户打开权限。
                    ActivityCompat.requestPermissions(LaunchActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    ActivityCompat.requestPermissions(LaunchActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }else{
                //权限已获取
                //创建文件夹用于保存裁剪的图片
                File file = new File(dirPath);
                if(!file.exists()){
                    file.mkdirs();
                }
                File fontPath = new File(ContextUtil.fontFullPath);
                if(!fontPath.exists()){
                    fontPath.mkdirs();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        startActivity(intent);
                        LaunchActivity.this.finish();
                    }
                }, 2000);

            }
        }else{
            //android版本低于23，权限已获取
            Log.e(TAG, "当前android版本"+Build.VERSION.SDK_INT+"低于23");
            //创建文件夹用于保存裁剪的图片
            File file = new File(dirPath);
            if(!file.exists()){
                file.mkdirs();
            }
            File fontPath = new File(ContextUtil.fontFullPath);
            if(!fontPath.exists()){
                fontPath.mkdirs();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                    LaunchActivity.this.finish();
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(LaunchActivity.this, "已授予读写手机存储权限", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //创建文件夹用于保存裁剪的图片
                    File file = new File(dirPath);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    File fontPath = new File(ContextUtil.fontFullPath);
                    if(!fontPath.exists()){
                        fontPath.mkdirs();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                            startActivity(intent);
                            LaunchActivity.this.finish();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(LaunchActivity.this, "拒绝授予读写手机存储权限将无法获取相册图片，请重新授权", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //判断外部存储卡是否存在
    private boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }
}
