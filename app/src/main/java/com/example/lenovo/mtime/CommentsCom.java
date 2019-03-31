package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.lenovo.mtime.adapter.ComComAdapter;
import com.example.lenovo.mtime.adapter.NewsComAdapter;
import com.example.lenovo.mtime.bean.ComCom;
import com.example.lenovo.mtime.bean.NewsCom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentsCom extends AppCompatActivity {

    RecyclerView recyclerView;
    String user_id;
    String commentsId;
    private List<ComCom> comComList;
    private ComComAdapter comComAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_com);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        context = this;

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        commentsId = intent.getStringExtra("commentsId");

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
                            .url("http://106.13.106.1/news/i/comment_list/"+commentsId)   //网址有待改动
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
            String status = jsonObject.getString("status");
            Log.d("hhh",num+"");
            comComList = gson.fromJson(list, new TypeToken<List<ComCom>>(){}.getType());
            Log.d("list",comComList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);

                comComAdapter = new ComComAdapter(comComList,user_id,context);

                recyclerView.setAdapter(comComAdapter);
            }
        });
    }
}
