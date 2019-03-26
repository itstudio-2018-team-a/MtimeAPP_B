package com.example.lenovo.mtime.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lenovo.mtime.NewsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.adapter.NewsAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.News;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private List<News> newsList;
    private NewsAdapter newsAdapter;
    String userName;
    private Button btn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_news,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        btn = (Button) view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), NewsDetail.class);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendRequestWithOkHttp();

    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://www/film/i/film_lsit")   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
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
            int num = jsonObject.getInt("num");
            String list = jsonObject.getString("list");
            String statues = jsonObject.getString("statues");

            newsList = gson.fromJson(list, new TypeToken<List<Movie>>(){}.getType());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);

                newsAdapter = new NewsAdapter(newsList,userName,getContext());

                recyclerView.setAdapter(newsAdapter);
            }
        });
    }
}
