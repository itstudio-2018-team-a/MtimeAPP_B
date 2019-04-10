package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.lenovo.mtime.adapter.NewsAdapter;
import com.example.lenovo.mtime.adapter.NewsComAdapter;
import com.example.lenovo.mtime.bean.News;
import com.example.lenovo.mtime.bean.NewsCom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
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
    String session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_comments);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        context = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        newsId = intent.getStringExtra("newsId");
        replys = intent.getStringExtra("replys");
        session = intent.getStringExtra("session");

        parseJSONWithGSON(replys);

    }
    private void parseJSONWithGSON(final String response){

        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(response);
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

                newsComAdapter = new NewsComAdapter(newsComList,user_id,context,newsId,session);

                recyclerView.setAdapter(newsComAdapter);
            }
        });
    }
}
