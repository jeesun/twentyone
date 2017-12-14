package com.jeesun.twentyone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jeesun.twentyone.util.ContextUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class PicActivity extends AppCompatActivity {
    private static final String TAG = PicActivity.class.getName();
    private ImageView ivPicture;
    private ProgressBar pb;

    private String picPath;
    private int picType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        //必须加这一行语句，否则Activity Dialog化后无法全屏。
        getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);//需要添加的语句
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ivPicture = findViewById(R.id.picture);
        pb = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        picPath = intent.getStringExtra("picPath");
        picType = intent.getIntExtra("picType", -1);

        final Integer picType = intent.getIntExtra("picType", -1);
        if(-1 != picType){
            if(ContextUtil.PIC_WEB == picType){
                Picasso.with(this).load(picPath).placeholder(R.drawable.bg_default).into(ivPicture);
            }else if(ContextUtil.PIC_LOCAL == picType){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
                ivPicture.setImageBitmap(bitmap);
            }
        }else{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
            ivPicture.setImageBitmap(bitmap);
        }

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicActivity.this.finish();
            }
        });

        ivPicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(-1 == picType || ContextUtil.PIC_LOCAL == picType){
                    return true;
                }
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(PicActivity.this);
                normalDialog.setTitle("提示");
                normalDialog.setMessage("确认保存该图片吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");

                                FileDownloader.getImpl().create(picPath)
                                        .setPath(ContextUtil.picSavePath + "/" + filename)
                                        .setListener(new FileDownloadListener() {
                                            @Override
                                            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                                if (totalBytes == -1) {
                                                    // chunked transfer encoding data
                                                    pb.setIndeterminate(true);
                                                } else {
                                                    pb.setMax(totalBytes);
                                                    pb.setProgress(soFarBytes);
                                                }
                                            }

                                            @Override
                                            protected void completed(BaseDownloadTask task) {
                                                Log.i(TAG, "下载完成");
                                                pb.setIndeterminate(false);
                                                pb.setMax(task.getSmallFileTotalBytes());
                                                pb.setProgress(task.getSmallFileSoFarBytes());
                                            }

                                            @Override
                                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            }

                                            @Override
                                            protected void error(BaseDownloadTask task, Throwable e) {

                                            }

                                            @Override
                                            protected void warn(BaseDownloadTask task) {

                                            }
                                        }).start();
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
                return true;
            }
        });
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
