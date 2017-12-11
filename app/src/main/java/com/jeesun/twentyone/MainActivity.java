package com.jeesun.twentyone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jeesun.twentyone.adapter.GridAdapter;
import com.jeesun.twentyone.model.PictureInfo;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    public final static String IMAGE_TYPE = "image/*";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private List<PictureInfo> pictureInfoList = new ArrayList<>();

    String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    //图片存储路径多了一层Pictures文件夹，方便MIUI的相册应用检测到。
    //String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TwentyOne/Pictures";
    String dirPath = downloadsDirectoryPath + "/Camera";

    //定义一个变量，来标识是否退出应用
    private static boolean isExit = false;
    private static Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.grid_swipe_refresh);
        recyclerView = findViewById(R.id.grid_recycler);

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);


        //读取sdcard下的TwentyOne文件夹下的图片
        setData();

        //设置recyclerview
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);
        recyclerView.setAdapter(adapter = new GridAdapter(pictureInfoList, MainActivity.this));

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };
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
                    Toast.makeText(this, "裁切后的图片保存在：" + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.main, menu);
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
    public void onRefresh() {
        updateData();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 读取sdcard下的TwentyOne文件夹下的图片
     */
    public void setData(){
        //读取sdcard下的TwentyOne文件夹
        File  scannerDirectory = new File(dirPath);
        if (scannerDirectory.isDirectory()) {
            for (File file : scannerDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith("_TwentyOne.jpg") || path.endsWith("_TwentyOne.jpeg") || path.endsWith("_TwentyOne.png")) {
                    pictureInfoList.add(new PictureInfo(path));
                }
            }
        }
    }

    private void updateData() {
        pictureInfoList.clear();

        setData();

        adapter.notifyDataSetChanged();

    }
}
