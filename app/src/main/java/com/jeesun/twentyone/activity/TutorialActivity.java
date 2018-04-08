package com.jeesun.twentyone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.jeesun.twentyone.R;

public class TutorialActivity extends AppCompatActivity {
    //private ImageView ivSample, ivTutorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        setTitle(R.string.tutorial);
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*ivSample = findViewById(R.id.sample);
        ivTutorial = findViewById(R.id.tutorial);*/

        //图片太大无法使用以下代码加载
        //ivSample.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sample));
        //ivTutorial.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.tutorial));

        //图片太大无法使用以下代码加载
        //Picasso.with(this).load(R.drawable.sample).into(ivSample);
        //Picasso.with(this).load(R.drawable.tutorial).into(ivTutorial);
        /*WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int screenHeight = outMetrics.heightPixels;

        Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.sample);

        temp = ImageUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.sample, temp.getWidth()/4, temp.getHeight()/4);
        ivSample.setImageBitmap(temp);*/

        /*Bitmap temp = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        temp = ImageUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.sample, temp.getWidth(), temp.getHeight());
        ivSample.setImageBitmap(temp);*/
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
