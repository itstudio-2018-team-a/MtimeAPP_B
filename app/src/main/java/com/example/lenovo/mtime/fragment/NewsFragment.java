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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private List<News> newsList = new ArrayList<>();
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

        sendRequestWithOkHttp(0);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.activity_main);

        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                newsList.clear();
                sendRequestWithOkHttp(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 结束刷新
                        pullToRefreshLayout.finishRefresh();
                    }
                }, 1000);
            }

            @Override
            public void loadMore() {
                sendRequestWithOkHttp(newsAdapter.getItemCount()+1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 1000);
            }
        });
        Bundle bundle = getArguments();
        if(bundle != null) {
            user_id = bundle.getString("user_id");
            session = bundle.getString("session");
        }
    }

    private void updateRecyclerView(int fromIndex, int toIndex) {
        List<News> newDatas = getDatas(fromIndex, toIndex);
        if (newDatas.size() > 0) {
            newsAdapter.updateList(newDatas, true);
        } else {
            newsAdapter.updateList(null, false);
        }
    }
    private List<News> getDatas(final int firstIndex, final int lastIndex) {
        List<News> resList = new ArrayList<>();
        for (int i = firstIndex; i < lastIndex; i++) {
            if (i < newsList.size()) {
                resList.add(newsList.get(i));
            }
        }
        return resList;
    }
//    //下拉刷新
//    private void refreshNews(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try{
//                    Thread.sleep(2000);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        newsList.clear();
//                        sendRequestWithOkHttp();
//                        newsAdapter.notifyDataSetChanged();
//                        swipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//        }).start();
//    }

    private void sendRequestWithOkHttp(final int newsNum){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10,TimeUnit.SECONDS)
                            .build();

                    String url = "http://132.232.78.106:8001/api/getNewsList/";
                    List<Map<String, String>> list_url = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();
                    map.put("head", String.valueOf(newsNum));
                    map.put("type", "0");
                    map.put("number", "5");
                    list_url.add(map);

                    url = getUrl(url, list_url);

                    Request request = new Request.Builder()
                            .url(url)   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    showResponse(responseDate,newsNum);

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

    private String getUrl(String url, List<Map<String, String>> list_url) {
        for (int i = 0; i < list_url.size(); i++) {
            Map<String, String> params = list_url.get(i);
            if (params != null) {
                Iterator<String> it = params.keySet().iterator();
                StringBuffer sb = null;
                while (it.hasNext()) {
                    String key = it.next();
                    String value = params.get(key);
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(value);
                }
                url += sb.toString();
            }
        }
        return url;
    }

    private void showResponse(final String response, final int newsNum){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            String list = jsonObject.getString("result");
            String state = jsonObject.getString("state");
            List<News> li = new ArrayList<News>();
            li = gson.fromJson(list, new TypeToken<List<News>>(){}.getType());

            newsList.addAll(li);
            Log.d("listhhh",newsList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //if (newsNum == 0)
               // {
                    //设置ui
                    LinearLayoutManager manager=new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(manager);
                    newsAdapter = new NewsAdapter(newsList,user_id,getContext(),session);
                    recyclerView.setAdapter(newsAdapter);
              //  }
//                else {
//                    //LinearLayoutManager manager=new LinearLayoutManager(getContext());
//                    //recyclerView.setLayoutManager(manager);
//                    newsAdapter = new NewsAdapter(newsList,user_id,getContext(),session);
//                    newsAdapter.notifyDataSetChanged();
//                }
            }
        });
    }
}
