package com.jeesun.twentyone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //String searchContent = getIntent().getStringExtra(SearchManager.QUERY);
        //Toast.makeText(this, searchContent, Toast.LENGTH_SHORT).show();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

    /*@Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar != null) {//mActionBarToolbar就是android.support.v7.widget.Toolbar
            toolbar.setTitle("");//设置为空，可以自己定义一个居中的控件，当做标题控件使用
        }
    }*/
}
