package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeesun.twentyone.activity.MainActivity;
import com.jeesun.twentyone.activity.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.interfaces.OnItemClickListener;
import com.jeesun.twentyone.model.PictureInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2017/12/9.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{
    private static final String TAG = GridAdapter.class.getName();
    private List<PictureInfo> pictureInfoList;
    private static Context context;
    private Boolean multi = false;
    private Map<Integer, Boolean> isSelected = null;
    private OnItemClickListener onItemClickListener;

    public GridAdapter(List<PictureInfo> pictureInfoList, Context context) {
        this.pictureInfoList = pictureInfoList;
        GridAdapter.context = context;
        Log.i(TAG, "2个参数pictureInfoList.size()=" + pictureInfoList.size());
    }

    public GridAdapter(List<PictureInfo> pictureInfoList, Context context, Boolean multi, HashMap<Integer, Boolean> isSelected) {
        this.pictureInfoList = pictureInfoList;
        GridAdapter.context = context;
        this.multi = multi;
        this.isSelected = isSelected;
        Log.i(TAG, isSelected.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View itemView = View.inflate(context, R.layout.list_item, null);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PictureInfo pictureInfo = pictureInfoList.get(position);
        holder.setData(pictureInfo, position);
    }

    @Override
    public int getItemCount() {
        if(null != pictureInfoList && pictureInfoList.size() > 0){
            return pictureInfoList.size();
        }
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.item_staggered_icon);
            textView = itemView.findViewById(R.id.item_staggered_name);
            checkBox = itemView.findViewById(R.id.item_checkbox);

            if (null == multi){
                multi = false;
            }
            if (multi){
                checkBox.setVisibility(View.VISIBLE);
            }else{
                checkBox.setVisibility(View.GONE);
            }
        }

        private void setData(final PictureInfo pictureInfo, int position){
            Log.i(TAG, "setData");
            if(null == pictureInfo){
                return;
            }
            if(null == pictureInfo.getUri() || "".equals(pictureInfo.getUri())){
                return;
            }
            /*if(pictureInfo.getUri().equals(imageView.getTag())){
                return;
            }*/

            imageView.setTag(pictureInfo.getUri());

            Picasso.with(context).load(new File(pictureInfo.getUri()))
                    .config(Bitmap.Config.RGB_565)
                    .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                    .placeholder(R.drawable.bg_default)
                    .tag(ContextUtil.PICASSO_TAG_LOCAL)
                    .into(imageView);

            if (null != onItemClickListener){
                itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(itemView, getAdapterPosition());
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View view) {
                        onItemClickListener.onItemLongClick(itemView, getAdapterPosition());
                        return true;
                    }
                });
            }

            if (isSelected != null){
                Log.i(TAG, isSelected.toString());
                Boolean val = isSelected.get(position);
                if (null == val){
                    Log.e(TAG, "val is null");
                }else{
                    Log.i(TAG, val.toString());
                }
                val = ((null == val) ? false : val);
                checkBox.setChecked(val);
                itemView.setSelected(val);
            }else{
                Log.i(TAG, "isSelected is null");
            }
        }
    }
}
