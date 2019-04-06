package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.lenovo.mtime.adapter.NewsAdapter;
import com.example.lenovo.mtime.adapter.NewsComAdapter;
import com.example.lenovo.mtime.bean.News;
import com.example.lenovo.mtime.bean.NewsCom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsComActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String user_id;
    String newsId;
    private List<NewsCom> newsComList;
    private NewsComAdapter newsComAdapter;
    private Context context;

    String replys;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_comments);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        context = this;

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        newsId = intent.getStringExtra("newsId");
        replys = intent.getStringExtra("replys");

        parseJSONWithGSON(replys);

    }
    private void parseJSONWithGSON(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            newsComList = gson.fromJson(response, new TypeToken<List<NewsCom>>(){}.getType());
            Log.d("listhhh",newsComList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);

                newsComAdapter = new NewsComAdapter(newsComList,user_id,context);

                recyclerView.setAdapter(newsComAdapter);
            }
        });
    }
}
