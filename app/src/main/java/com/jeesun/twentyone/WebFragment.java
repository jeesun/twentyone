package com.jeesun.twentyone;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jeesun.twentyone.adapter.MyGridAdapter;
import com.jeesun.twentyone.customui.FlowLayout;
import com.jeesun.twentyone.interfaces.RequestServes;
import com.jeesun.twentyone.model.PicCategory;
import com.jeesun.twentyone.model.ResultMsg;
import com.jeesun.twentyone.model.WebPicInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by simon on 2017/12/13.
 */

public class WebFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = WebFragment.class.getName();
    private View rootView;
    private FlowLayout flowLayout;
    private SwipeRefreshLayout srlGrid;
    private RecyclerView recyclerView;
    //private WebGridAdapter adapter;
    private MyGridAdapter adapter;
    private List<PicCategory> categoryList = new ArrayList<>();
    private List<String> categoryNameList = new ArrayList<>();
    private List<WebPicInfo> webPicInfoList = new ArrayList<>();
    private int start = 0;
    private int count = 10;

    private String[] mDatas;
    View header;

    private int categoryId = 26;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(srlGrid.isRefreshing()){
                    srlGrid.setRefreshing(false);
                }
            }
        }, 5000);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        srlGrid = rootView.findViewById(R.id.grid_swipe_refresh);
        recyclerView = rootView.findViewById(R.id.grid_recycler);
        header = inflater.inflate(R.layout.header, container, false);
        flowLayout = header.findViewById(R.id.flow_layout);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
        //retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()).build();
        RequestServes requestServes = retrofit.create(RequestServes.class);
        Call<ResultMsg> call = requestServes.getCategory();
        call.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                Log.i(TAG, "data got from web");
                categoryList = JSON.parseArray(JSON.toJSONString(response.body().getData()), PicCategory.class);
                for (int i=0; i<categoryList.size(); i++){
                    categoryNameList.add(categoryList.get(i).getName());
                }
                Log.i(TAG, categoryNameList.toString());
                if(srlGrid.isRefreshing()){
                    srlGrid.setRefreshing(false);
                }
                mDatas = categoryNameList.toArray(new String[categoryNameList.size()]);
                setFlowData();
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {

            }
        });


        srlGrid.setProgressBackgroundColorSchemeResource(android.R.color.white);
        srlGrid.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        srlGrid.setOnRefreshListener(this);

        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);


        //当我们确定Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)，
        //并通过Adapter的增删改插方法去刷新RecyclerView，而不是通过notifyDataSetChanged()。
        //（其实可以直接设置为true，当需要改变宽高的时候就用notifyDataSetChanged()去整体刷新一下）
        recyclerView.setHasFixedSize(true);
        //adapter = new WebGridAdapter(webPicInfoList, getActivity());
        adapter = new MyGridAdapter(webPicInfoList, getActivity());
        recyclerView.setAdapter(adapter);


        srlGrid.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "data is refreshing");
                srlGrid.setRefreshing(true);
                onRefresh();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断是否滑动到底部
                if(RecyclerView.SCROLL_STATE_IDLE == newState){

                    if(null == webPicInfoList){
                        return;
                    }
                    if(webPicInfoList.size() <= 0){
                       return;
                    }
                    //RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
                    //RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
                    if(recyclerView.canScrollVertically(1)){
                        Picasso.with(getActivity()).resumeTag(ContextUtil.PICASSO_TAG_WEB);
                    }else{
                        Retrofit retrofit2 = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(new OkHttpClient()).build();
                        RequestServes requestServes2 = retrofit2.create(RequestServes.class);
                        start += count;
                        Log.i(TAG, "start=" + start);
                        Call<ResultMsg> call2 = requestServes2.getPicsByCategory(categoryId, start, count);
                        call2.enqueue(new Callback<ResultMsg>() {
                            @Override
                            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                                Log.i(TAG, response.body().getData().toString());
                                //webPicInfoList.clear();
                                List<WebPicInfo> newData = JSON.parseArray(JSON.toJSONString(response.body().getData()), WebPicInfo.class);
                                webPicInfoList.addAll(newData);
                                Log.i(TAG, "webPicInfoList's size is "+webPicInfoList.size());
                                adapter.notifyItemRangeInserted(start, count);
                                if(srlGrid.isRefreshing()){
                                    srlGrid.setRefreshing(false);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultMsg> call, Throwable t) {

                            }
                        });
                    }

                }else{
                    Picasso.with(getActivity()).pauseTag(ContextUtil.PICASSO_TAG_WEB);
                }
            }
        });

        return rootView;
    }



    private void updateData() {
        Log.i(TAG, "data is updating");

        //恢复位置0
        start = 0;

        Retrofit retrofit2 = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()).build();
        RequestServes requestServes2 = retrofit2.create(RequestServes.class);
        Call<ResultMsg> call2 = requestServes2.getPicsByCategory(categoryId, start, count);
        call2.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                Log.i(TAG, "data got from web");
                List<WebPicInfo> newData = JSON.parseArray(JSON.toJSONString(response.body().getData()), WebPicInfo.class);
                Log.i(TAG, "new data size is " + newData.size());
                //清空现有数据
                webPicInfoList.clear();
                webPicInfoList.addAll(newData);
                Log.i(TAG, "webPicInfoList's size is "+webPicInfoList.size());
                adapter.notifyDataSetChanged();
                //adapter.addDatas(webPicInfoList);
                adapter.setHeaderView(header);
                if(srlGrid.isRefreshing()){
                    srlGrid.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(srlGrid.isRefreshing()){
                    srlGrid.setRefreshing(false);
                }
            }
        }, 5000);
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "刷新数据");
        updateData();
    }

    private void setFlowData(){
        Random random = new Random();

        // 循环添加TextView到容器
        for (int i = 0; i < mDatas.length; i++) {
            final TextView view = new TextView(getActivity());
            view.setText(mDatas[i]);
            view.setTextColor(Color.WHITE);
            view.setPadding(5, 5, 5, 5);
            view.setGravity(Gravity.CENTER);
            view.setTextSize(14);

            final int iCopy = i;
            // 设置点击事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), view.getText().toString(), Toast.LENGTH_SHORT).show();
                    categoryId = categoryList.get(iCopy).getId();
                    srlGrid.setRefreshing(true);
                    onRefresh();
                }
            });

            // 设置彩色背景
            GradientDrawable normalDrawable = new GradientDrawable();
            normalDrawable.setShape(GradientDrawable.RECTANGLE);
            int a = 255;
            int r = 50 + random.nextInt(150);
            int g = 50 + random.nextInt(150);
            int b = 50 + random.nextInt(150);
            normalDrawable.setColor(Color.argb(a, r, g, b));

            // 设置按下的灰色背景
            GradientDrawable pressedDrawable = new GradientDrawable();
            pressedDrawable.setShape(GradientDrawable.RECTANGLE);
            pressedDrawable.setColor(Color.GRAY);

            // 背景选择器
            StateListDrawable stateDrawable = new StateListDrawable();
            stateDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateDrawable.addState(new int[]{}, normalDrawable);

            // 设置背景选择器到TextView上
            view.setBackground(stateDrawable);

            flowLayout.addView(view);
        }
    }
}
