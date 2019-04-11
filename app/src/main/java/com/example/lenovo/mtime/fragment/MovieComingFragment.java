package com.example.lenovo.mtime.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
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

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class MovieComingFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private List<Movie> movies=new ArrayList<>();
    private MovieAdapter movieAdapter;
    private String user_id;
    private String session;
    PullToRefreshLayout pullToRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.moviecomingfragment,container,false);
        movies.clear();


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
                movies.clear();
                sendRequestWithOkHttp(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // 结束刷新
                        pullToRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }

            @Override
            public void loadMore() {
                sendRequestWithOkHttp(movieAdapter.getItemCount()+1);
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
        if(bundle != null) {
            user_id = bundle.getString("user_id");
            session = bundle.getString("session");
        }

        //测试用
//        for (int i = 0; i <= 10 ; i++){
//            Movie movie = new Movie();
//            movie.setMark("9.8");
//            movie.setMovieId(i+"");
//            movie.setMovieName("复仇者联盟4：终局之战");
//            movie.setTime("2019.06.07");
//            movies.add(movie);
//        }

//        LinearLayoutManager manager=new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(manager);
//        movieAdapter = new MovieAdapter(getContext(), movies);
//        recyclerView.setAdapter(movieAdapter);
    }


    private void sendRequestWithOkHttp(final int newsNum){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
//                    OkHttpClient client = new OkHttpClient();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10,TimeUnit.SECONDS)
                            .build();

                    String url = "http://132.232.78.106:8001/api/getFilmList/";
                    List<Map<String, String>> list_url = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();
                    map.put("head", String.valueOf(newsNum));
                    map.put("type", "0");
                    map.put("number", "10");
                    list_url.add(map);

                    url = getUrl(url, list_url);

                    Request request = new Request.Builder()
                            .url(url)
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
                                Toast.makeText(getContext(),"连接超时，请检查网络设置",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(getContext(),"连接异常，请检查网络设置",Toast.LENGTH_SHORT).show();
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


    private void showResponse(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int state = jsonObject.getInt("state");
            String list = jsonObject.getString("result");
//            String status = jsonObject.getString("status");
            List<Movie> li = new ArrayList<Movie>();
            li = gson.fromJson(list, new TypeToken<List<Movie>>(){}.getType());

            movies.addAll(li);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);
                movieAdapter = new MovieAdapter(getContext(), movies,user_id,session);
                recyclerView.setAdapter(movieAdapter);
            }
        });
    }
}
