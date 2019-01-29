package com.jeesun.twentyone.customui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.model.SoPicInfo;
import com.jeesun.twentyone.model.WebPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Created by simon on 2017/12/15.
 */

public class SavePicDialog extends Dialog {
    private static final String TAG = SavePicDialog.class.getName();
    private Context context;
    private View rootView;
    private ImageView ivPicture;
    private TextView tvProgressHint;
    private ProgressBar pb;
    private Button btnCancelSave;
    private Button btnSave;

    private WebPicInfo webPicInfo;
    private SoPicInfo soPicInfo;

    public SavePicDialog(@NonNull Context context, int themeResId, WebPicInfo webPicInfo) {
        super(context, themeResId);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.customui_dialog_save_pic, null);
        ivPicture = rootView.findViewById(R.id.picture);
        tvProgressHint = rootView.findViewById(R.id.progress_hint);
        pb = rootView.findViewById(R.id.progress_bar);
        btnCancelSave = rootView.findViewById(R.id.cancel_save_pic);
        btnSave = rootView.findViewById(R.id.save_pic);
        this.webPicInfo = webPicInfo;
    }

    public SavePicDialog(@NonNull Context context, int themeResId, SoPicInfo soPicInfo) {
        super(context, themeResId);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.customui_dialog_save_pic, null);
        ivPicture = rootView.findViewById(R.id.picture);
        tvProgressHint = rootView.findViewById(R.id.progress_hint);
        pb = rootView.findViewById(R.id.progress_bar);
        btnCancelSave = rootView.findViewById(R.id.cancel_save_pic);
        btnSave = rootView.findViewById(R.id.save_pic);
        this.soPicInfo = soPicInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(rootView);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvProgressHint.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

                //没有下一行代码，无法从ImageView对象中获取图像；
                //ivPicture.setDrawingCacheEnabled(true);
                //Bitmap bitmap = Bitmap.createBitmap(ivPicture.getDrawingCache());
                //bitmap = ImageUtil.cropBitmap(bitmap);
                final String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");
                /*ImageUtil.saveBitmap(context, TAG, ContextUtil.picSavePath, filename, bitmap);
                //清空画图缓冲区，否则，下一次从ImageView对象中获取的图像，还是原来的图像。
                ivPicture.setDrawingCacheEnabled(false);

                //当设置setIndeterminate(true)参数为真时，进度条采用不明确显示进度的‘模糊模式’，
                //当设置setIndeterminate(false)参数为假时, 进度条不采用‘模糊模式’，而采用明确显示进度的‘明确模式’，
                pb.setIndeterminate(false);
                pb.setMax(100);
                pb.setProgress(100);

                tvProgressHint.setText(context.getText(R.string.download_success));*/
                String picPath;
                if(null != webPicInfo){
                    picPath = webPicInfo.getUrl();
                }else if(null != soPicInfo){
                    if(null != soPicInfo.getImg()){
                        picPath = soPicInfo.getImg();
                    }else if(null != soPicInfo.getThunmb()){
                        picPath = soPicInfo.getThunmb();
                    }else if(null != soPicInfo.getThumb_bak()){
                        picPath = soPicInfo.getThumb_bak();
                    }else if(null != soPicInfo.get_thunmb()){
                        picPath = soPicInfo.get_thunmb();
                    }else if(null != soPicInfo.get_thumb_bak()){
                        picPath = soPicInfo.get_thumb_bak();
                    }else{
                        return;
                    }
                }else{
                    return;
                }
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
                                Log.i(TAG, context.getText(R.string.download_success).toString());
                                //当设置setIndeterminate(true)参数为真时，进度条采用不明确显示进度的‘模糊模式’，
                                //当设置setIndeterminate(false)参数为假时, 进度条不采用‘模糊模式’，而采用明确显示进度的‘明确模式’，
                                pb.setIndeterminate(false);
                                pb.setMax(task.getSmallFileTotalBytes());
                                pb.setProgress(task.getSmallFileSoFarBytes());
                                tvProgressHint.setText(context.getText(R.string.download_success));
                                //Toast.makeText(PicActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                                Bitmap localBitmap = BitmapFactory.decodeFile(ContextUtil.picSavePath + "/" + filename);
                                localBitmap = ImageUtil.cropBitmap(localBitmap);
                                ImageUtil.saveBitmap(context, TAG, ContextUtil.picSavePath, filename, localBitmap);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismiss();
                                    }
                                }, 1000);
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

        btnCancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setIvPicture(String picPath){
        Log.i(TAG, "picPath=" + picPath);
        Picasso.get()
                .load(picPath)
                .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                .placeholder(R.drawable.bg_default)
                .error(R.drawable.bg_default)
                .into(ivPicture);
    }

    public View getRootView() {
        return rootView;
    }
}
