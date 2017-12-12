package com.jeesun.twentyone;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jeesun.twentyone.util.ContextUtil;

public class FontsActivity extends AppCompatActivity {
    private static final String TAG = FontsActivity.class.getName();
    private long mTaskId;
    BroadcastReceiver receiver;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fonts);
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button btnDownload = findViewById(R.id.download);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //创建下载任务,downloadUrl就是下载链接
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://www.1001freefonts.com/d/5021/bebas-neue.zip"));
                //指定下载路径和下载文件名
                request.setDestinationInExternalPublicDir(ContextUtil.fontPath, "bebas-neue.zip");
                //获取下载管理器
                downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                //将下载任务加入下载队列，否则不会进行下载
                mTaskId = downloadManager.enqueue(request);
                //注册广播接收者，监听下载状态
                registerReceiver(receiver,
                        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        //广播接受者，接收下载状态
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkDownloadStatus();//检查下载状态
            }
        };
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

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i(TAG, ">>>下载暂停");
                    Toast.makeText(FontsActivity.this, "下载暂停", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Log.i(TAG, ">>>下载延迟");
                    Toast.makeText(FontsActivity.this, "下载延迟", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    Log.i(TAG, ">>>正在下载");
                    Toast.makeText(FontsActivity.this, "正在下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i(TAG, ">>>下载完成");
                    //下载完成安装APK
                    //downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + versionName;
                    //installAPK(new File(downloadPath));
                    Toast.makeText(FontsActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.i(TAG, ">>>下载失败");
                    Toast.makeText(FontsActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
