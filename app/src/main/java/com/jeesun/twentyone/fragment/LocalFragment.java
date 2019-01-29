package com.jeesun.twentyone.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jeesun.twentyone.R;
import com.jeesun.twentyone.activity.MainActivity;
import com.jeesun.twentyone.activity.PicActivity;
import com.jeesun.twentyone.adapter.GridAdapter;
import com.jeesun.twentyone.interfaces.OnItemClickListener;
import com.jeesun.twentyone.model.PictureInfo;
import com.jeesun.twentyone.util.ContextUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by simon on 2017/12/13.
 */

public class LocalFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = LocalFragment.class.getName();
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout llMultiBtnGroup;
    private Button btnDelete, btnSelectAll, btnInverse, btnCancel;
    private GridAdapter adapter, multiAdapter;
    private List<PictureInfo> pictureInfoList = new ArrayList<>();
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    String dirPath = ContextUtil.picSavePath;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment_local, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.grid_swipe_refresh);
        recyclerView = rootView.findViewById(R.id.grid_recycler);
        llMultiBtnGroup = rootView.findViewById(R.id.multi_btn_group);
        btnDelete = rootView.findViewById(R.id.btn_delete);
        btnSelectAll = rootView.findViewById(R.id.btn_select_all);
        btnInverse = rootView.findViewById(R.id.btn_inverse);
        btnCancel = rootView.findViewById(R.id.btn_cancel);

        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);

        //读取sdcard下的TwentyOne文件夹下的图片
        setData();

        //当我们确定Item的改变不会影响RecyclerView的宽高的时候可以设置setHasFixedSize(true)，
        //并通过Adapter的增删改插方法去刷新RecyclerView，而不是通过notifyDataSetChanged()。
        //（其实可以直接设置为true，当需要改变宽高的时候就用notifyDataSetChanged()去整体刷新一下）
        recyclerView.setHasFixedSize(true);
        /*StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        sglm.setReverseLayout(false);
        recyclerView.setLayoutManager(sglm);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        multiAdapter = new GridAdapter(pictureInfoList, getActivity(), true, isSelected);
        adapter = new GridAdapter(pictureInfoList, getActivity());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Picasso.get().resumeTag(ContextUtil.PICASSO_TAG_LOCAL);
                }else{
                    Picasso.get().pauseTag(ContextUtil.PICASSO_TAG_LOCAL);
                }
            }
        });

        multiAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "position=" + position);
                if(!isSelected.get(position)){
                    isSelected.put(position, true); // 修改map的值保存状态
                    multiAdapter.notifyItemChanged(position);
                }else {
                    isSelected.put(position, false); // 修改map的值保存状态
                    multiAdapter.notifyItemChanged(position);
                }
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.i(TAG, "position=" + position);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), PicActivity.class);
                intent.putExtra("picPath", pictureInfoList.get(position).getUrl());
                intent.putExtra("picType", ContextUtil.PIC_LOCAL);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(getActivity());
                normalDialog.setTitle("提示");
                normalDialog.setMessage("确认删除该图片吗？");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                                File picFile = new File(pictureInfoList.get(position).getUrl());
                                if(picFile.exists() && picFile.isFile()){
                                    if(picFile.delete()){
                                        updateData(pictureInfoList.get(position), position);
                                        //Toast.makeText(context, "图片已被删除，请刷新确认", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "图片删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
                List<Integer> keyList = new ArrayList<>();
                Iterator iter = isSelected.entrySet().iterator();
                while (iter.hasNext()){
                    Map.Entry entry = (Map.Entry) iter.next();
                    Integer key = (Integer) entry.getKey();
                    Boolean val = (Boolean) entry.getValue();
                    if (val){
                        keyList.add(key);
                    }
                }
                Log.i(TAG, keyList.toString());

                ListIterator<Integer> lit=keyList.listIterator();//使用ListIterator
                while (lit.hasNext()) {
                    Log.i(TAG, "next is " + lit.next());
                }

                while (lit.hasPrevious()){
                    int key = lit.previous();
                    Log.i(TAG, "key=" + key);
                    File picFile = new File(pictureInfoList.get(key).getUrl());
                    if(picFile.exists() && picFile.isFile()){
                        if(picFile.delete()){
                            Log.i(TAG, "删除成功");
                        }else{
                            Toast.makeText(getActivity(), "图片删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                updateData();
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
                Log.i(TAG, isSelected.toString());
            }
        });

        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "select all");
                for(int i = 0; i < pictureInfoList.size(); i++) {
                    isSelected.put(i, true);
                }
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
                Log.i(TAG, isSelected.toString());
                multiAdapter.notifyDataSetChanged();
            }
        });

        btnInverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "inverse");
                for (int i = 0; i < pictureInfoList.size(); i++){
                    if (isSelected.get(i)){
                        isSelected.put(i, false);
                    }else{
                        isSelected.put(i, true);
                    }
                }
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
                Log.i(TAG, isSelected.toString());
                multiAdapter.notifyDataSetChanged();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "cancel");
                for(int i = 0; i < pictureInfoList.size(); i++) {
                    if (isSelected.get(i)){
                        isSelected.put(i, false);
                    }
                }
                Log.i(TAG, "hashCode=" + isSelected.hashCode());
                Log.i(TAG, isSelected.toString());
                multiAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "执行onRefresh方法");
        updateData();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 读取sdcard下的TwentyOne文件夹下的图片
     */
    public void setData(){
        Log.i(TAG, "执行setData方法");
        //读取sdcard下的TwentyOne文件夹
        File scannerDirectory = new File(dirPath);
        if (scannerDirectory.isDirectory()) {
            int count = 0;
            for (File file : scannerDirectory.listFiles()) {
                String path = file.getAbsolutePath();
                if (path.endsWith("_TwentyOne.jpg") || path.endsWith("_TwentyOne.jpeg") || path.endsWith("_TwentyOne.png") ||
                        path.endsWith("_card.jpg") || path.endsWith("_card.jpeg") || path.endsWith("_card.png")) {
                    pictureInfoList.add(new PictureInfo(path));
                    isSelected.put(count, false);
                    count++;
                }
            }
        }
    }

    public void updateData() {
        Log.i(TAG, "执行updateData方法");
        pictureInfoList.clear();
        isSelected.clear();

        setData();

        adapter.notifyDataSetChanged();
        multiAdapter.notifyDataSetChanged();
    }

    public void updateData(PictureInfo pictureInfo, Integer position) {
        pictureInfoList.remove(pictureInfo);
        isSelected.remove(position);
        adapter.notifyDataSetChanged();
        multiAdapter.notifyDataSetChanged();
    }

    public void refreshData() {
        Log.i(TAG, "执行refreshData方法");
        swipeRefreshLayout.setRefreshing(true);
        updateData();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void changeLayout(Boolean isMulti){
        Log.i(TAG, "执行changeLayout方法");
        if (isMulti){
            recyclerView.setAdapter(multiAdapter);
            llMultiBtnGroup.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setAdapter(adapter);
            llMultiBtnGroup.setVisibility(View.GONE);
        }
    }
}
