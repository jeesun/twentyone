package com.jeesun.twentyone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jeesun.twentyone.util.ColorUtil;
import com.jeesun.twentyone.util.ContextUtil;
import com.jeesun.twentyone.util.ImageUtil;
import com.jeesun.twentyone.util.TypefaceUtil;

import java.util.Calendar;

public class BusinessCardActivity extends AppCompatActivity {
    public final static int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = BusinessCardActivity.class.getName();
    private ImageView mSourImage;
    private ImageView mWartermarkImage;
    private Button btnBuildInWhite, btnBuildInTransparent;
    private EditText etFontSizeCorner, etFontSizeCenter, etLeftTop, etLeftBottom, etRightTop, etRightBottom, etCenter;
    private Button btnMake, btnSave;
    private Spinner spFontColor, spTypefaceCorner, spTypefaceCenter;
    private static final int padding = 12;
    private int fontColor = Color.BLACK;
    private Bitmap bmDefault, bmWhite, bmTransparent;

    String dirPath = ContextUtil.picSavePath;

    private String typefaceCornerUri="方正魏碑简体", typefaceCenterUri="连笔签名字体";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);
        setTitle(R.string.business_card);

        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        finView();

        initView();
    }

    private void finView() {
        mSourImage =  findViewById(R.id.sour_pic);
        mWartermarkImage =  findViewById(R.id.wartermark_pic);
        etFontSizeCorner = findViewById(R.id.font_size_corner);
        etFontSizeCenter = findViewById(R.id.font_size_center);
        etLeftTop = findViewById(R.id.left_top);
        etLeftBottom = findViewById(R.id.left_bottom);
        etRightTop = findViewById(R.id.right_top);
        etRightBottom = findViewById(R.id.right_bottom);
        etCenter = findViewById(R.id.center);
        btnMake = findViewById(R.id.make);
        btnSave = findViewById(R.id.save);
        spFontColor = findViewById(R.id.font_color);
        spTypefaceCorner = findViewById(R.id.typeface_corner);
        spTypefaceCenter = findViewById(R.id.typeface_center);
        btnBuildInWhite = findViewById(R.id.build_in_white);
        btnBuildInTransparent = findViewById(R.id.build_in_transparent);
    }

    private void initView(){
        bmDefault = BitmapFactory.decodeResource(getResources(), R.drawable.bg_default);
        bmWhite = BitmapFactory.decodeResource(getResources(), R.drawable.bg_white);
        bmTransparent = BitmapFactory.decodeResource(getResources(), R.drawable.bg_transparent);

        //Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);
        mSourImage.setImageBitmap(bmDefault);

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

        spTypefaceCorner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] typefaces = getResources().getStringArray(R.array.typeface);
                typefaceCornerUri = TypefaceUtil.getInstance().getTypefaceUri(typefaces[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spTypefaceCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] typefaces = getResources().getStringArray(R.array.typeface);
                typefaceCenterUri = TypefaceUtil.getInstance().getTypefaceUri(typefaces[i]);
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
                //没有下一行代码，无法从ImageView对象中获取图像；
                mSourImage.setDrawingCacheEnabled(true);
                //Bitmap sourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_business_card);
                Bitmap sourBitmap = mSourImage.getDrawingCache();

                Integer fontSizeCorner, fontSizeCenter;
                if(!"".equals(etFontSizeCorner.getText().toString())){
                    fontSizeCorner = Integer.parseInt(etFontSizeCorner.getText().toString());
                    if(fontSizeCorner <= 0){
                        fontSizeCorner = 36;
                    }
                }else{
                    fontSizeCorner = 36;
                }
                if(!"".equals(etFontSizeCenter.getText().toString())){
                    fontSizeCenter = Integer.parseInt(etFontSizeCenter.getText().toString());
                    if(fontSizeCenter <= 0){
                        fontSizeCenter = 36;
                    }
                }else{
                    fontSizeCenter = 36;
                }

                Bitmap textBitmap = ImageUtil.drawTextToLeftTop(BusinessCardActivity.this, sourBitmap, etLeftTop.getText().toString(), fontSizeCorner, typefaceCornerUri, fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToRightBottom(BusinessCardActivity.this, textBitmap, etRightBottom.getText().toString(), fontSizeCorner, typefaceCornerUri, fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToRightTop(BusinessCardActivity.this, textBitmap, etRightTop.getText().toString(), fontSizeCorner,typefaceCornerUri, fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToLeftBottom(BusinessCardActivity.this, textBitmap, etLeftBottom.getText().toString(), fontSizeCorner, typefaceCornerUri, fontColor, padding, padding);
                textBitmap = ImageUtil.drawTextToCenterAndTextStartFromCenter(BusinessCardActivity.this, textBitmap, etCenter.getText().toString(), fontSizeCenter, typefaceCenterUri, fontColor);

                mWartermarkImage.setImageBitmap(textBitmap);

                hideKeyboard();

                //清空画图缓冲区，否则，下一次从ImageView对象中获取的图像，还是原来的图像。
                mSourImage.setDrawingCacheEnabled(false);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //没有下一行代码，无法从ImageView对象中获取图像；
                mWartermarkImage.setDrawingCacheEnabled(true);
                Bitmap textBitmap = Bitmap.createBitmap(mWartermarkImage.getDrawingCache());
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), "card.png");
                ImageUtil.saveBitmap(BusinessCardActivity.this, TAG, dirPath, filename, textBitmap);
                //清空画图缓冲区，否则，下一次从ImageView对象中获取的图像，还是原来的图像。
                mWartermarkImage.setDrawingCacheEnabled(false);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            BusinessCardActivity.this.finish();
            return false;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.specific_symbol:
                Intent intent = new Intent(BusinessCardActivity.this, SpecificSymbolActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.business_card, menu);
        return true;
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
