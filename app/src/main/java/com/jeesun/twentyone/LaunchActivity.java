package com.jeesun.twentyone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class LaunchActivity extends AppCompatActivity {
    private static final String TAG = LaunchActivity.class.getName();
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //图片存储路径多了一层Pictures文件夹，方便MIUI的相册应用检测到。
    //String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TwentyOne/Pictures";
    String dirPath = downloadsDirectoryPath + "/Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "当手机系统大于 23");
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
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }else{
            //android版本低于23，权限已获取
            Log.e(TAG, "当手机系统大于 23");
            //创建文件夹用于保存裁剪的图片
            File file = new File(dirPath);
            if(!file.exists()){
                file.mkdirs();
            }
            Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(intent);
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
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
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
}
