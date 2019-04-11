package com.example.lenovo.mtime.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lenovo.mtime.NewsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.adapter.NewsAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.News;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private List<News> newsList;
    private NewsAdapter newsAdapter;
    String user_id;
    String session;
    PullToRefreshLayout pullToRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_news,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.activity_main);
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        newsList.clear();
                        sendRequestWithOkHttp();
                        newsAdapter.notifyDataSetChanged();
                        // 结束刷新
                        pullToRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });
        Bundle bundle = getArguments();
        if(bundle != null){
            user_id = bundle.getString("user_id");
            session = bundle.getString("session");
        }



        sendRequestWithOkHttp();

    }


    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getNewsList/")   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    showResponse(responseDate);

                }catch (final Exception e){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(getContext(),"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(getContext(),"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(getContext(),"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void showResponse(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            String list = jsonObject.getString("result");
            String state = jsonObject.getString("state");
            newsList = gson.fromJson(list, new TypeToken<List<News>>(){}.getType());
            Log.d("listhhh",newsList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);
                newsAdapter = new NewsAdapter(newsList,user_id,getContext(),session);
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }
}
