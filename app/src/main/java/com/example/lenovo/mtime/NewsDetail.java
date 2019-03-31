package com.example.lenovo.mtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetail extends AppCompatActivity {

    String newsId;
    String user_id;
    FloatingActionButton fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        newsId = intent.getStringExtra("newsId");
        user_id = intent.getStringExtra("user_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id == null)
                {
                    Toast.makeText(NewsDetail.this, "您还没有登录，不能发表评论", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent1 = new Intent(NewsDetail.this,MakeNewsCom .class);
                    intent1.putExtra("user_id",user_id);
                    intent1.putExtra("newsId",newsId);
                    startActivity(intent1);
                }
            }
        });

        sendRequestWithOkHttp();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        final MenuItem item = menu.findItem(R.id.comment);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.comment:
                Intent intent = new Intent(NewsDetail.this, NewsComActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("newsId",newsId);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://106.13.106.1/news/i/news/?news_id="+newsId)
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
            String title = jsonObject.getString("title");
            String news_id = jsonObject.getString("news_id");
            String pub_time = jsonObject.getString("pub_time");
            String update_time = jsonObject.getString("update_time");
            String comment_num = jsonObject.getString("comment_num");
            String picture = jsonObject.getString("picure");
            String status = jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui

            }
        });
    }
}
