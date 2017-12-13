package com.jeesun.twentyone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jeesun.twentyone.util.ContextUtil;
import com.squareup.picasso.Picasso;

public class PicActivity extends AppCompatActivity {
    private ImageView ivPicture;
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

        Intent intent = getIntent();
        String picPath = intent.getStringExtra("picPath");

        Integer picType = intent.getIntExtra("picType", -1);
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
