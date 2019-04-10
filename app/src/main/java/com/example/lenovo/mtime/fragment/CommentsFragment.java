package com.example.lenovo.mtime.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.CommentsAdapter;
import com.example.lenovo.mtime.bean.Comments;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentsFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    private List<Comments> commentsList;
    private CommentsAdapter commentsAdapter;
    String user_id;
    String session;
    SwipeRefreshLayout swipeRefresh;
    private GridLayoutManager mLayoutManager;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int lastVisibleItem = 0;
    private final int PAGE_COUNT = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments,container,false);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //下拉刷新功能
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNews();
            }
        });
        Bundle bundle = getArguments();
        if(bundle != null){
            user_id = bundle.getString("user_id");
            session = bundle.getString("session");
        }


        sendRequestWithOkHttp();
        //initRefreshLayout();

    }

    //下拉刷新
    private void refreshNews(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentsList.clear();
                        sendRequestWithOkHttp();
                        commentsAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
//    private void initRecyclerView() {
//        commentsAdapter = new CommentsAdapter(getDatas(0, PAGE_COUNT), user_id,getContext(),session, getDatas(0, PAGE_COUNT).size() > 0 ? true : false);
//        mLayoutManager = new GridLayoutManager(getContext(), 1);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setAdapter(commentsAdapter);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (commentsAdapter.isFadeTips() == false && lastVisibleItem + 1 == commentsAdapter.getItemCount()) {
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateRecyclerView(commentsAdapter.getRealLastPosition(), commentsAdapter.getRealLastPosition() + PAGE_COUNT);
//                            }
//                        }, 500);
//                    }
//
//                    if (commentsAdapter.isFadeTips() == true && lastVisibleItem + 2 == commentsAdapter.getItemCount()) {
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateRecyclerView(commentsAdapter.getRealLastPosition(), commentsAdapter.getRealLastPosition() + PAGE_COUNT);
//                            }
//                        }, 500);
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
//            }
//        });
//    }


//    private void initRefreshLayout() {
//        swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
//                android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // 设置可见
//                swipeRefresh.setRefreshing(true);
//                // 重置adapter的数据源为空
//                commentsAdapter.resetDatas();
//                // 获取第第0条到第PAGE_COUNT（值为10）条的数据
//                updateRecyclerView(0, PAGE_COUNT);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // 模拟网络加载时间，设置不可见
//                        swipeRefresh.setRefreshing(false);
//                    }
//                }, 1000);
//            }
//        });
//    }
    private List<Comments> getDatas(final int firstIndex, final int lastIndex) {
        List<Comments> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < commentsList.size()) {
                resList.add(commentsList.get(i));
            }
        }
        return resList;
    }
//    private void updateRecyclerView(int fromIndex, int toIndex) {
//        List<Comments> newDatas = getDatas(fromIndex, toIndex);
//        if (newDatas.size() > 0) {
//            commentsAdapter.updateList(newDatas, true);
//        } else {
//            commentsAdapter.updateList(null, false);
//        }
//    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getHotFilmReview/")   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.e("response",response.toString());
                    String responseDate = response.body().string();
                    showResponse(responseDate);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response){
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.e("response",response);
            String list = jsonObject.getString("result");
            Log.d("commentsList",list);

            commentsList = gson.fromJson(list, new TypeToken<List<Comments>>(){}.getType());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                //设置ui

                //initRecyclerView();
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);
                commentsAdapter = new CommentsAdapter(commentsList,user_id,getContext(),session);

                recyclerView.setAdapter(commentsAdapter);
            }
        });
    }
}
