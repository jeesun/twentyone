package com.jeesun.twentyone;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeesun.twentyone.adapter.ViewPagerAdapter;
import com.jeesun.twentyone.model.PictureInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    public final static String IMAGE_TYPE = "image/*";

    private ViewPager vpViewPager;
    private FragmentPagerAdapter pagerAdapter;
    private ImageView ivCursor;
    private TextView tvOne, tvTwo;

    String dirPath = ContextUtil.picSavePath;

    //定义一个变量，来标识是否退出应用
    private static boolean isExit = false;
    private static Handler mHandler;

    private List<Fragment> fragmentList;
    private List<String> titleList;

    private LocalFragment localFragment;
    private WebFragment webFragment;

    private int mOffset, mOneDis, mCurrentIndex;
    private MenuItem miSearch, miPick, miBusinessCard, miTutorial, miAbout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileDownloader.setup(this);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        vpViewPager = findViewById(R.id.view_pager);
        ivCursor = findViewById(R.id.cursor);
        tvOne = findViewById(R.id.viewpager_tv_one);
        tvTwo = findViewById(R.id.viewpager_tv_two);

        toolbar.setTitle(R.string.app_name_short);
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolbar);

        //初始化指示器位置
        initCursorPosition();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };

        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();

        localFragment = new LocalFragment();
        webFragment = new WebFragment();
        fragmentList.add(localFragment);
        fragmentList.add(webFragment);
        titleList.add("本地");
        titleList.add("网络");
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        vpViewPager.setAdapter(pagerAdapter);

        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);

        //页面改变监听器
        vpViewPager.addOnPageChangeListener(this);
        //初始默认第一页
        vpViewPager.setCurrentItem(0);
    }

    private void initCursorPosition() {
        //获取指示器图片宽度
        int cursorWidth = BitmapFactory.decodeResource(getResources(), R.drawable.line).getWidth();

        //获取分辨率宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;

        //计算偏移量
        mOffset = (screenWidth / 2 - cursorWidth) /2;

        //设置动画初始位置
        Matrix matrix = new Matrix();
        matrix.postTranslate(mOffset, 0);
        ivCursor.setImageMatrix(matrix);

        //计算指示器图片的偏移距离
        mOneDis = screenWidth / 2; //页卡1 ——》页卡2 偏移量
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_IMAGE_CAPTURE){
                //Toast.makeText(this, "获取相册", Toast.LENGTH_SHORT).show();
                Uri uri = data.getData();
                startCrop(uri.toString());
            }
        }
        if (resultCode == RESULT_OK) {
            //裁切成功
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri croppedFileUri = UCrop.getOutput(data);
                //获取默认的下载目录
                //String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());
                Log.i("downloadsDirectoryPath", dirPath);
                File saveFile = new File(dirPath, filename);
                //保存下载的图片
                FileInputStream inStream = null;
                FileOutputStream outStream = null;
                FileChannel inChannel = null;
                FileChannel outChannel = null;
                try {
                    inStream = new FileInputStream(new File(croppedFileUri.getPath()));
                    outStream = new FileOutputStream(saveFile);
                    inChannel = inStream.getChannel();
                    outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    //Toast.makeText(this, "裁切后的图片保存在：" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "图片已保存到相册的相机文件夹中，主页已自动刷新", Toast.LENGTH_SHORT).show();
                    localFragment.refreshData();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        outChannel.close();
                        outStream.close();
                        inChannel.close();
                        inStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //裁切失败
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "裁切图片失败", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        miSearch = menu.findItem(R.id.search);
        miPick = menu.findItem(R.id.pick);
        miBusinessCard = menu.findItem(R.id.business_card);
        miTutorial = menu.findItem(R.id.tutorial);
        miAbout = menu.findItem(R.id.about);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.pick:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType(IMAGE_TYPE);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                break;
            case R.id.business_card:
                intent = new Intent(MainActivity.this, BusinessCardActivity.class);
                startActivity(intent);
                break;
            case R.id.tutorial:
                intent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            if(!isExit){
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            }else{
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startCrop(String imageUri) {
        Uri sourceUri = Uri.parse(imageUri);
        //裁剪后保存到文件中
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "TwentyOne.png"));
        UCrop.of(sourceUri, destinationUri).withAspectRatio(18, 9).withMaxResultSize(1440, 720).start(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.viewpager_tv_one:
                vpViewPager.setCurrentItem(0);
                break;
            case R.id.viewpager_tv_two:
                vpViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //指示器图片动画设置
        Animation anim = null;
        switch (position){
            case 0:
                if(1 == mCurrentIndex){
                    anim = new TranslateAnimation(mOneDis, 0, 0, 0);
                }
                //ActionBar关闭显示搜索图标
                if(null != miSearch){
                    miSearch.setVisible(false);
                    miPick.setVisible(true);
                    miBusinessCard.setVisible(true);
                    miTutorial.setVisible(true);
                    miAbout.setVisible(true);
                }
                break;
            case 1:
                if(0 == mCurrentIndex){
                    anim = new TranslateAnimation(mOffset, mOneDis, 0, 0);
                }
                //ActionBar显示搜索图标
                if(null != miSearch){
                    miSearch.setVisible(true);
                    miPick.setVisible(false);
                    miBusinessCard.setVisible(false);
                    miTutorial.setVisible(false);
                    miAbout.setVisible(false);
                }
                break;
            default:
                break;
        }
        mCurrentIndex = position;
        anim.setFillAfter(true); //Ture:图片停在动画结束位置
        anim.setDuration(300);
        ivCursor.startAnimation(anim);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void updateLocalData(PictureInfo pictureInfo){
        localFragment.updateData(pictureInfo);
    }
}
