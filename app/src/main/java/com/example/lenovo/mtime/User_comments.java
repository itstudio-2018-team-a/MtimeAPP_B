package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.adapter.CommentsAdapter;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class User_comments extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Comments> comments = new ArrayList<>();
    private CommentsAdapter commentsAdapter;
    private String user_id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

        recyclerView = findViewById(R.id.Recycleview);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        url = "106.13.106.1/account/i/user/comments_filmreview/" + user_id;
        sendRequestWithOkHttp();

//        LinearLayoutManager manager=new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(manager);
//
//        commentsAdapter = new CommentsAdapter(comments);
//
//        recyclerView.setAdapter(commentsAdapter);
    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String cookie = response.header("Set-Cookie");  //获取cookie

                    //将cookie储存到sharedpreference
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("cookie",cookie);
                    editor.apply();

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

            comments = gson.fromJson(list, new TypeToken<List<Comments>>(){}.getType());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui

//                LinearLayoutManager manager=new LinearLayoutManager(this);
//                recyclerView.setLayoutManager(manager);
//
//                commentsAdapter = new CommentsAdapter(this, comments);
//
//                recyclerView.setAdapter(commentsAdapter);

                //写到个人中心的评论，未知bug未解决
            }
        });
    }
}
