package com.jeesun.twentyone.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeesun.twentyone.activity.PicActivity;
import com.jeesun.twentyone.R;
import com.jeesun.twentyone.customui.SavePicDialog;
import com.jeesun.twentyone.model.SoPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by simon on 2017/12/17.
 */

public class SoGridAdapter extends BaseRecyclerAdapter<SoPicInfo> {
    private Context context;

    public SoGridAdapter(List<SoPicInfo> mDatas, Context context) {
        super(mDatas, context);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.web_list_item,parent,false);
        return new MyHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, SoPicInfo data) {
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

        public void setData(final SoPicInfo soPicInfo){
            if(soPicInfo.getId() == imageView.getTag()){
                return;
            }

            //Log.i(TAG, webPicInfo.getImg_1366_768());
            String picPath = null;
            if(null != soPicInfo.get_thunmb()){
                picPath = soPicInfo.get_thunmb();
            }else if(null != soPicInfo.get_thumb_bak()){
                picPath = soPicInfo.get_thumb_bak();
            }else if(null != soPicInfo.getThunmb()){
                picPath = soPicInfo.getThunmb();
            }else if(null != soPicInfo.getThumb_bak()){
                picPath = soPicInfo.getThumb_bak();
            }else if(null != soPicInfo.getImg()){
                picPath = soPicInfo.getImg();
            }

            Picasso.with(context)
                    .load(picPath)
                    .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                    .placeholder(R.drawable.bg_default)
                    .tag(ContextUtil.PICASSO_TAG_WEB)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PicActivity.class);
                    String picPath = null;
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
                    }
                    intent.putExtra("picPath", picPath);
                    intent.putExtra("picType", ContextUtil.PIC_WEB);
                    context.startActivity(intent);
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    SavePicDialog dialog = new SavePicDialog(context, R.style.dialogStyle, soPicInfo);
                    String picPath = null;
                    if(null != soPicInfo.get_thunmb()){
                        picPath = soPicInfo.get_thunmb();
                    }else if(null != soPicInfo.get_thumb_bak()){
                        picPath = soPicInfo.get_thumb_bak();
                    }else if(null != soPicInfo.getThunmb()){
                        picPath = soPicInfo.getThunmb();
                    }else if(null != soPicInfo.getThumb_bak()){
                        picPath = soPicInfo.getThumb_bak();
                    }else if(null != soPicInfo.getImg()){
                        picPath = soPicInfo.getImg();
                    }
                    dialog.setIvPicture(picPath);
                    dialog.show();

                    return true;
                }
            });
        }
    }
}
