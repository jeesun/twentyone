package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeesun.twentyone.MainActivity;
import com.jeesun.twentyone.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.model.PictureInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by simon on 2017/12/9.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{
    private static final String TAG = GridAdapter.class.getName();
    private List<PictureInfo> pictureInfoList;
    private static Context context;

    public GridAdapter(List<PictureInfo> pictureInfoList, Context context) {
        this.pictureInfoList = pictureInfoList;
        GridAdapter.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.list_item, null);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PictureInfo pictureInfo = pictureInfoList.get(position);
        holder.setData(pictureInfo);
    }

    @Override
    public int getItemCount() {
        if(null != pictureInfoList && pictureInfoList.size() > 0){
            return pictureInfoList.size();
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

        public void setData(final PictureInfo pictureInfo){
            if(null == pictureInfo){
                return;
            }
            if(null == pictureInfo.getUri() || "".equals(pictureInfo.getUri())){
                return;
            }
            if(pictureInfo.getUri().equals(imageView.getTag())){
                return;
            }
            imageView.setTag(pictureInfo.getUri());

            /*Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFilePath(pictureInfo.getUri(), 540, 270);

            imageView.setImageBitmap(bitmap);*/
            Picasso.with(context).load(new File(pictureInfo.getUri()))
                    .config(Bitmap.Config.RGB_565)
                    .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                    .placeholder(R.drawable.bg_default)
                    .tag(ContextUtil.PICASSO_TAG_LOCAL)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PicActivity.class);
                    intent.putExtra("picPath", pictureInfo.getUri());
                    intent.putExtra("picType", ContextUtil.PIC_LOCAL);
                    context.startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(context);
                    normalDialog.setTitle("提示");
                    normalDialog.setMessage("确认删除该图片吗？");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                    File picFile = new File(pictureInfo.getUri());
                                    if(picFile.exists() && picFile.isFile()){
                                        if(picFile.delete()){
                                            if(context instanceof MainActivity){
                                                ((MainActivity)context).updateLocalData(pictureInfo);
                                            }
                                            //Toast.makeText(context, "图片已被删除，请刷新确认", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(context, "图片删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
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
