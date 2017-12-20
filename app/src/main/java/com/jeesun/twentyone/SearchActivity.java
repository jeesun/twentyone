package com.jeesun.twentyone;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.SearchView;

import com.jeesun.twentyone.adapter.SoGridAdapter;
import com.jeesun.twentyone.interfaces.RequestServes;
import com.jeesun.twentyone.model.SoPicInfo;
import com.jeesun.twentyone.model.SoResultMsg;
import com.jeesun.twentyone.util.ContextUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getName();
    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;

    private List<SoPicInfo> soPicInfoList = new ArrayList<>();
    private SoGridAdapter adapter;

    private int start = 0;
    private int count = 10;

    private static String queryWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        FileDownloader.setup(this);
        //String searchContent = getIntent().getStringExtra(SearchManager.QUERY);
        //Toast.makeText(this, searchContent, Toast.LENGTH_SHORT).show();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        //标题栏返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            //由于自定义ActionBar的Style，一并改变了返回按钮的颜色，此处代码用于设置返回按钮的颜色
            Drawable upArrow = getResources().getDrawable(R.drawable.ic_menu_arrow_back);
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.grid_recycler);


        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);


        //当我们确定Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)，
        //并通过Adapter的增删改插方法去刷新RecyclerView，而不是通过notifyDataSetChanged()。
        //（其实可以直接设置为true，当需要改变宽高的时候就用notifyDataSetChanged()去整体刷新一下）
        recyclerView.setHasFixedSize(false);
        //adapter = new WebGridAdapter(webPicInfoList, getActivity());
        adapter = new SoGridAdapter(soPicInfoList, SearchActivity.this);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryWord = query;
                start = 0;
                //Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://image.so.com")
                        //retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(new OkHttpClient()).build();
                RequestServes requestServes = retrofit.create(RequestServes.class);
                Call<SoResultMsg> call = requestServes.getSoPicsByKeyWord(query, start, count, 4);
                call.enqueue(new Callback<SoResultMsg>() {
                    @Override
                    public void onResponse(Call<SoResultMsg> call, Response<SoResultMsg> response) {
                        //清空现有数据
                        soPicInfoList.clear();
                        soPicInfoList.addAll(response.body().getList());
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<SoResultMsg> call, Throwable t) {

                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(RecyclerView.SCROLL_STATE_IDLE == newState){
                    if(null == soPicInfoList){
                        return;
                    }
                    if(soPicInfoList.size() <= 0){
                        return;
                    }
                    Picasso.with(SearchActivity.this).resumeTag(ContextUtil.PICASSO_TAG_WEB);
                    //RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
                    //RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
                    if(!recyclerView.canScrollVertically(1)){
                        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://image.so.com")
                                //retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient()).build();
                        RequestServes requestServes = retrofit.create(RequestServes.class);
                        start += count;
                        Call<SoResultMsg> call = requestServes.getSoPicsByKeyWord(queryWord, start, count, 4);
                        call.enqueue(new Callback<SoResultMsg>() {
                            @Override
                            public void onResponse(Call<SoResultMsg> call, Response<SoResultMsg> response) {
                                //Log.i(TAG, response.body().toString());
                                soPicInfoList.addAll(response.body().getList());
                                adapter.notifyItemRangeInserted(start, count);
                            }

                            @Override
                            public void onFailure(Call<SoResultMsg> call, Throwable t) {

                            }
                        });
                    }
                }else{
                    Picasso.with(SearchActivity.this).pauseTag(ContextUtil.PICASSO_TAG_WEB);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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

    /*@Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        if (toolbar != null) {//mActionBarToolbar就是android.support.v7.widget.Toolbar
            toolbar.setTitle("");//设置为空，可以自己定义一个居中的控件，当做标题控件使用
        }
    }*/
}
