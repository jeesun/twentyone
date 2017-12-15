package com.jeesun.twentyone.customui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
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

    public SavePicDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.customui_dialog_save_pic, null);
        ivPicture = rootView.findViewById(R.id.picture);
        tvProgressHint = rootView.findViewById(R.id.progress_hint);
        pb = rootView.findViewById(R.id.progress_bar);
        btnCancelSave = rootView.findViewById(R.id.cancel_save_pic);
        btnSave = rootView.findViewById(R.id.save_pic);
    }

    public SavePicDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.customui_dialog_save_pic, null);
        ivPicture = rootView.findViewById(R.id.picture);
        tvProgressHint = rootView.findViewById(R.id.progress_hint);
        pb = rootView.findViewById(R.id.progress_bar);
        btnCancelSave = rootView.findViewById(R.id.cancel_save_pic);
        btnSave = rootView.findViewById(R.id.save_pic);
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
                pb.setIndeterminate(true);

                //没有下一行代码，无法从ImageView对象中获取图像；
                ivPicture.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(ivPicture.getDrawingCache());
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");
                ImageUtil.saveBitmap(context, TAG, ContextUtil.picSavePath, filename, bitmap);
                //清空画图缓冲区，否则，下一次从ImageView对象中获取的图像，还是原来的图像。
                ivPicture.setDrawingCacheEnabled(false);

                //当设置setIndeterminate(true)参数为真时，进度条采用不明确显示进度的‘模糊模式’，
                //当设置setIndeterminate(false)参数为假时, 进度条不采用‘模糊模式’，而采用明确显示进度的‘明确模式’，
                pb.setIndeterminate(false);
                pb.setMax(100);
                pb.setProgress(100);

                tvProgressHint.setText(context.getText(R.string.download_success));
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
        Picasso.with(context)
                .load(picPath)
                .resize(ImageUtil.dp2px(context, 540), ImageUtil.dp2px(context, 270))
                .placeholder(R.drawable.bg_default)
                .into(ivPicture);
    }

    public View getRootView() {
        return rootView;
    }
}
