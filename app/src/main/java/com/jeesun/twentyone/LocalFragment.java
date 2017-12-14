package com.jeesun.twentyone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeesun.twentyone.adapter.GridAdapter;
import com.jeesun.twentyone.model.PictureInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 2017/12/13.
 */

public class LocalFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private List<PictureInfo> pictureInfoList = new ArrayList<>();

    String dirPath = ContextUtil.picSavePath;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_local, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.grid_swipe_refresh);
        recyclerView = rootView.findViewById(R.id.grid_recycler);

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        //读取sdcard下的TwentyOne文件夹下的图片
        setData();

        //当我们确定Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)，
        //并通过Adapter的增删改插方法去刷新RecyclerView，而不是通过notifyDataSetChanged()。
        //（其实可以直接设置为true，当需要改变宽高的时候就用notifyDataSetChanged()去整体刷新一下）
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);
        recyclerView.setAdapter(adapter = new GridAdapter(pictureInfoList, getActivity()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Picasso.with(getActivity()).resumeTag(ContextUtil.PICASSO_TAG_LOCAL);
                }else{
                    Picasso.with(getActivity()).pauseTag(ContextUtil.PICASSO_TAG_LOCAL);
                }
            }
        });

        return rootView;
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
        File scannerDirectory = new File(dirPath);
        if (scannerDirectory.isDirectory()) {
            for (File file : scannerDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith("_TwentyOne.jpg") || path.endsWith("_TwentyOne.jpeg") || path.endsWith("_TwentyOne.png") ||
                        path.endsWith("_card.jpg") || path.endsWith("_card.jpeg") || path.endsWith("_card.png")) {
                    pictureInfoList.add(new PictureInfo(path));
                }
            }
        }
    }

    public void updateData() {
        pictureInfoList.clear();

        setData();

        adapter.notifyDataSetChanged();

    }

    public void updateData(PictureInfo pictureInfo) {
        pictureInfoList.remove(pictureInfo);
        adapter.notifyDataSetChanged();
    }

    public void refreshData() {
        swipeRefreshLayout.setRefreshing(true);
        updateData();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
