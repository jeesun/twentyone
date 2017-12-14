package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeesun.twentyone.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.model.WebPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by simon on 2017/12/13.
 */

public class WebGridAdapter extends RecyclerView.Adapter<WebGridAdapter.ViewHolder>{
    private static final String TAG = GridAdapter.class.getName();
    private List<WebPicInfo> webPicInfoList;
    private static Context context;

    public WebGridAdapter(List<WebPicInfo> webPicInfoList, Context context) {
        this.webPicInfoList = webPicInfoList;
        WebGridAdapter.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.web_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        /*View itemView = View.inflate(context, R.layout.web_list_item, null);
        ViewHolder holder = new ViewHolder(itemView);*/
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebPicInfo webPicInfo = webPicInfoList.get(position);
        holder.setData(webPicInfo);
    }

    @Override
    public int getItemCount() {
        if(null != webPicInfoList && webPicInfoList.size() > 0){
            return webPicInfoList.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.item_staggered_icon);
            textView = itemView.findViewById(R.id.item_staggered_name);
        }

        public void setData(final WebPicInfo webPicInfo){
            if(webPicInfo.getId() == imageView.getTag()){
                return;
            }
            imageView.setTag(webPicInfo.getId());

            //Log.i(TAG, webPicInfo.getImg_1366_768());

            Picasso.with(context).load(webPicInfo.getImg_1366_768()).placeholder(R.drawable.bg_default).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PicActivity.class);
                    intent.putExtra("picPath", webPicInfo.getImg_1366_768());
                    intent.putExtra("picType", ContextUtil.PIC_WEB);
                    context.startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(context);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("确认保存该图片吗？");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");

                                    FileDownloader.getImpl().create(webPicInfo.getImg_1366_768())
                                            .setPath(ContextUtil.picSavePath + "/" + filename)
                                            .setListener(new FileDownloadListener() {
                                                @Override
                                                protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                                }

                                                @Override
                                                protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                                }

                                                @Override
                                                protected void completed(BaseDownloadTask task) {
                                                    Log.i(TAG, "下载完成");
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
                    return false;
                }
            });
        }
    }
}
