package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jeesun.twentyone.activity.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.customui.SavePicDialog;
import com.jeesun.twentyone.model.WebPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.jeesun.twentyone.util.WallPaperUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

/**
 * Created by simon on 2017/12/16.
 */

public class MyGridAdapter extends BaseRecyclerAdapter<WebPicInfo> {
    private Context context;

    public MyGridAdapter(List<WebPicInfo> mDatas, Context context) {
        super(mDatas, context);
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.web_list_item,parent,false);
        return new MyHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, WebPicInfo data) {
        if (viewHolder instanceof MyHolder){
            ((MyHolder)viewHolder).setData(data);
        }
    }

    private class MyHolder extends BaseRecyclerAdapter.Holder{
        View itemView;
        ImageView imageView;
        TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.item_staggered_icon);
            textView = itemView.findViewById(R.id.item_staggered_name);
        }

        public void setData(final WebPicInfo webPicInfo){
            /*if(webPicInfo.getCid() == imageView.getTag()){
                return;
            }*/

            if (!webPicInfo.getUrl().equals(imageView.getTag(R.id.item_staggered_icon))){
                imageView.setTag(R.id.item_staggered_icon, webPicInfo.getUrl());

                //Picasso加载大量图片的时候，回收资源太快了。只能缓存Glide
                /*Picasso.get()
                        .load(webPicInfo.getUrl())
                        .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                        .placeholder(R.drawable.bg_default)
                        .error(R.drawable.bg_default)
                        .tag(ContextUtil.PICASSO_TAG_WEB)
                        .into(imageView);*/

                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.bg_default)
                        .error(R.drawable.bg_default)
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .skipMemoryCache(false)
                        .override(540, 270);

                RequestListener mListener = new RequestListener<Drawable>(){

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                };

                Glide.with(context)
                        .load(webPicInfo.getUrl())
                        .addListener(mListener)
                        .apply(options)
                        .preload(540, 270);

                Glide.with(context)
                        .load(WallPaperUtil.assignSize(webPicInfo.getUrl(), WallPaperUtil.DEFAULT_BDR))
                        .addListener(mListener)
                        .apply(options)
                        .into(imageView);


                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, PicActivity.class);
                        intent.putExtra("picPath", webPicInfo.getUrl());
                        intent.putExtra("picType", ContextUtil.PIC_WEB);
                        context.startActivity(intent);
                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        SavePicDialog dialog = new SavePicDialog(context, R.style.dialogStyle, webPicInfo);
                        dialog.setIvPicture(webPicInfo.getUrl());
                        dialog.show();

                        return true;
                    }
                });
            }
        }
    }
}
