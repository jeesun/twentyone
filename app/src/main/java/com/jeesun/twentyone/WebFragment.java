package com.jeesun.twentyone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.jeesun.twentyone.adapter.WebGridAdapter;
import com.jeesun.twentyone.interfaces.RequestServes;
import com.jeesun.twentyone.model.PicCategory;
import com.jeesun.twentyone.model.ResultMsg;
import com.jeesun.twentyone.model.WebPicInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by simon on 2017/12/13.
 */

public class WebFragment extends Fragment {
    private static final String TAG = WebFragment.class.getName();
    private View rootView;
    private RecyclerView recyclerView;
    private WebGridAdapter adapter;
    private List<WebPicInfo> webPicInfoList = new ArrayList<>();
    private int start = 0;
    private int count = 10;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_web, container, false);
        recyclerView = rootView.findViewById(R.id.grid_recycler);

        //设置recyclerview
        recyclerView.setHasFixedSize(true);
        adapter = new WebGridAdapter(webPicInfoList, getActivity());
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()).build();
        RequestServes requestServes = retrofit.create(RequestServes.class);
        Call<ResultMsg> call = requestServes.getCategory();
        call.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                List<PicCategory> array = JSON.parseArray(JSON.toJSONString(response.body().getData()), PicCategory.class);
                //Log.i(TAG, "array size is " + array.size());
                List<String> categoryNameList = new ArrayList<>();
                for (int i=0; i<array.size(); i++){
                    categoryNameList.add(array.get(i).getName());
                }
                Log.i(TAG, categoryNameList.toString());
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {

            }
        });

        Retrofit retrofit2 = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient()).build();
        RequestServes requestServes2 = retrofit2.create(RequestServes.class);
        Call<ResultMsg> call2 = requestServes2.getPicsByCategory(26, start, count);
        call2.enqueue(new Callback<ResultMsg>() {
            @Override
            public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                //webPicInfoList.clear();
                webPicInfoList.addAll(JSON.parseArray(JSON.toJSONString(response.body().getData()), WebPicInfo.class));
                Log.i(TAG, "webPicInfoList's size is "+webPicInfoList.size());
                adapter.notifyItemRangeInserted(start, count);
            }

            @Override
            public void onFailure(Call<ResultMsg> call, Throwable t) {

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断是否滑动到底部
                if(RecyclerView.SCROLL_STATE_IDLE == newState){
                    Retrofit retrofit2 = new Retrofit.Builder().baseUrl("http://cdn.apc.360.cn")
//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了 默认支持Gson解析
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(new OkHttpClient()).build();
                    RequestServes requestServes2 = retrofit2.create(RequestServes.class);
                    start += count;
                    Log.i(TAG, "start=" + start);
                    Call<ResultMsg> call2 = requestServes2.getPicsByCategory(26, start, count);
                    call2.enqueue(new Callback<ResultMsg>() {
                        @Override
                        public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {
                            Log.i(TAG, response.body().getData().toString());
                            //webPicInfoList.clear();
                            List<WebPicInfo> newData = JSON.parseArray(JSON.toJSONString(response.body().getData()), WebPicInfo.class);
                            webPicInfoList.addAll(newData);
                            Log.i(TAG, "webPicInfoList's size is "+webPicInfoList.size());
                            adapter.notifyItemRangeInserted(start, count);
                        }

                        @Override
                        public void onFailure(Call<ResultMsg> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return rootView;
    }
}
