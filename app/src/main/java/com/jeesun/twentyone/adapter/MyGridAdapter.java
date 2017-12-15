package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeesun.twentyone.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.customui.SavePicDialog;
import com.jeesun.twentyone.model.WebPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

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

    class MyHolder extends BaseRecyclerAdapter.Holder{
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
            if(webPicInfo.getId() == imageView.getTag()){
                return;
            }

            //Log.i(TAG, webPicInfo.getImg_1366_768());

            Picasso.with(context)
                    .load(webPicInfo.getImg_1366_768())
                    .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                    .placeholder(R.drawable.bg_default)
                    .tag(ContextUtil.PICASSO_TAG_WEB)
                    .into(imageView);

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
                    SavePicDialog dialog = new SavePicDialog(context, R.style.dialogStyle, webPicInfo);
                    dialog.setIvPicture(webPicInfo.getImg_1366_768());
                    dialog.show();

                    return true;
                }
            });
        }
    }
}
