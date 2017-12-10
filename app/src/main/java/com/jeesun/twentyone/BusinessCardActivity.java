package com.jeesun.twentyone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jeesun.twentyone.util.ColorUtil;
import com.jeesun.twentyone.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class BusinessCardActivity extends AppCompatActivity {
    public final static int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = BusinessCardActivity.class.getName();
    private ImageView mSourImage;
    private ImageView mWartermarkImage;
    private Button btnBuildInWhite, btnBuildInTransparent;
    private EditText etFontSize, etLeftTop, etLeftBottom, etRightTop, etRightBottom, etCenter;
    private Button btnMake, btnSave;
    private Spinner spFontColor;
    private static final int padding = 12;
    private int fontColor = Color.BLACK;
    private Bitmap bmWhite, bmTransparent;

    String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //图片存储路径多了一层Pictures文件夹，方便MIUI的相册应用检测到。
    //String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TwentyOne/Pictures";
    String dirPath = downloadsDirectoryPath + "/Camera";

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
        etFontSize = findViewById(R.id.font_size);
        etLeftTop = findViewById(R.id.left_top);
        etLeftBottom = findViewById(R.id.left_bottom);
        etRightTop = findViewById(R.id.right_top);
        etRightBottom = findViewById(R.id.right_bottom);
        etCenter = findViewById(R.id.center);
        btnMake = findViewById(R.id.make);
        btnSave = findViewById(R.id.save);
        spFontColor = findViewById(R.id.font_color);
        btnBuildInWhite = findViewById(R.id.build_in_white);
        btnBuildInTransparent = findViewById(R.id.build_in_transparent);


        bmWhite = BitmapFactory.decodeResource(getResources(), R.drawable.bg_white);
        bmTransparent = BitmapFactory.decodeResource(getResources(), R.drawable.bg_transparent);

        //Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);
        mSourImage.setImageBitmap(bmWhite);

        btnBuildInWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSourImage.setImageBitmap(bmWhite);
                Toast.makeText(BusinessCardActivity.this, "已切换至白色背景", Toast.LENGTH_SHORT).show();
            }
        });

        btnBuildInTransparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSourImage.setImageBitmap(bmTransparent);
                Toast.makeText(BusinessCardActivity.this, "已切换至透明背景", Toast.LENGTH_SHORT).show();
            }
        });

        mSourImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });


        spFontColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] fontColors = getResources().getStringArray(R.array.font_color);
                //Toast.makeText(BusinessCardActivity.this, fontColors[i], Toast.LENGTH_SHORT).show();
                fontColor = ColorUtil.getInstance().getColorCode(fontColors[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Bitmap waterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.weixin);

        //Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        //watermarkBitmap = ImageUtil.createWaterMaskLeftBottom(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskRightBottom(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskLeftTop(this, watermarkBitmap, waterBitmap, 0, 0);
        //watermarkBitmap = ImageUtil.createWaterMaskRightTop(this, watermarkBitmap, waterBitmap, 0, 0);

        btnMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSourImage.setDrawingCacheEnabled(true);
                //Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);
                Bitmap sourBitmap = mSourImage.getDrawingCache();

                Integer fontSize;
                if(!"".equals(etFontSize.getText().toString())){
                    fontSize = Integer.parseInt(etFontSize.getText().toString());
                    if(fontSize <= 0){
                        fontSize = 36;
                    }
                }else{
                    fontSize = 36;
                }


                Bitmap textBitmap = ImageUtil.drawTextToLeftTop(BusinessCardActivity.this, sourBitmap, etLeftTop.getText().toString(), fontSize, "fonts/方正魏碑简体.ttf", fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToRightBottom(BusinessCardActivity.this, textBitmap, etRightBottom.getText().toString(), fontSize, "fonts/方正魏碑简体.ttf", fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToRightTop(BusinessCardActivity.this, textBitmap, etRightTop.getText().toString(), fontSize,"fonts/方正魏碑简体.ttf", fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToLeftBottom(BusinessCardActivity.this, textBitmap, etLeftBottom.getText().toString(), fontSize, "fonts/方正魏碑简体.ttf", fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToCenterAndTextStartFromCenter(BusinessCardActivity.this, textBitmap, etCenter.getText().toString(), fontSize, "fonts/方正魏碑简体.ttf", fontColor);

                mWartermarkImage.setImageBitmap(textBitmap);

                hideKeyboard();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                if(null != data){
                    mSourImage.setImageURI(data.getData());
                }
            }
        }
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (null != this.getCurrentFocus().getWindowToken()) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
