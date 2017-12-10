package com.jeesun.twentyone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jeesun.twentyone.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class BusinessCardActivity extends AppCompatActivity {
    private static final String TAG = BusinessCardActivity.class.getName();
    private ImageView mSourImage;
    private ImageView mWartermarkImage;
    private EditText etLeftTop, etLeftBottom, etRightTop, etRightBottom, etCenter;
    private Button btnMake, btnSave;


    //图片存储路径多了一层Pictures文件夹，方便MIUI的相册应用检测到。
    String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TwentyOne/Pictures";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);
        setTitle(R.string.business_card);

        initView();
    }

    private void initView(){
        mSourImage =  findViewById(R.id.sour_pic);
        mWartermarkImage =  findViewById(R.id.wartermark_pic);
        etLeftTop = findViewById(R.id.left_top);
        etLeftBottom = findViewById(R.id.left_bottom);
        etRightTop = findViewById(R.id.right_top);
        etRightBottom = findViewById(R.id.right_bottom);
        etCenter = findViewById(R.id.center);
        btnMake = findViewById(R.id.make);
        btnSave = findViewById(R.id.save);

        Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);
        mSourImage.setImageBitmap(sourBitmap);

        //Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.weixin);

        //Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        //watermarkBitmap = ImageUtil.createWaterMaskLeftBottom(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskRightBottom(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskLeftTop(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskRightTop(this, watermarkBitmap, waterBitmap, 0, 0);

        btnMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);

                Bitmap textBitmap = ImageUtil.drawTextToLeftTop(BusinessCardActivity.this, sourBitmap, etLeftTop.getText().toString(), 36, "fonts/方正魏碑简体.ttf", Color.BLACK, 10, 10);
                textBitmap = ImageUtil.drawTextToRightBottom(BusinessCardActivity.this, textBitmap, etRightBottom.getText().toString(), 36, "fonts/方正魏碑简体.ttf", Color.BLACK, 10, 10);
                textBitmap = ImageUtil.drawTextToRightTop(BusinessCardActivity.this, textBitmap, etRightTop.getText().toString(), 36,"fonts/方正魏碑简体.ttf", Color.BLACK, 10, 10);
                textBitmap = ImageUtil.drawTextToLeftBottom(BusinessCardActivity.this, textBitmap, etLeftBottom.getText().toString(), 36, "fonts/方正魏碑简体.ttf", Color.BLACK, 10, 10);
                textBitmap = ImageUtil.drawTextToCenter(BusinessCardActivity.this, textBitmap, etCenter.getText().toString(), 36, "fonts/方正魏碑简体.ttf", Color.BLACK);

                mWartermarkImage.setImageBitmap(textBitmap);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWartermarkImage.setDrawingCacheEnabled(true);
                Bitmap textBitmap = Bitmap.createBitmap(mWartermarkImage.getDrawingCache());
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");
                saveBitmap(dirPath, filename, textBitmap);
            }
        });

    }

    public void saveBitmap(String dirPath, String picName, Bitmap bm) {
        Log.e(TAG, "保存图片");
        File f = new File(dirPath, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
            Toast.makeText(BusinessCardActivity.this, "已经保存，请回主页刷新确认", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(BusinessCardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(BusinessCardActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
