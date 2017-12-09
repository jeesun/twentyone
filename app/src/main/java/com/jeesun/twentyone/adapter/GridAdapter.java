package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.model.PictureInfo;

import java.util.List;

/**
 * Created by simon on 2017/12/9.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
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

        public void setData(PictureInfo pictureInfo){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(pictureInfo.getUri(), options);
            imageView.setImageBitmap(bitmap);

        }
    }
}
