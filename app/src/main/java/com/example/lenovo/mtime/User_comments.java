package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.adapter.CommentsAdapter;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.adapter.UserMovComAdapter;
import com.example.lenovo.mtime.adapter.UserNewsComAdapter;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.User;
import com.example.lenovo.mtime.bean.UserMovCom;
import com.example.lenovo.mtime.bean.UserNewsCom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class User_comments extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<UserMovCom> movComs = new ArrayList<>();
    private List<UserNewsCom> newsComs = new ArrayList<>();
    private UserMovComAdapter userMovComAdapter;
    private UserNewsComAdapter userNewsComAdapter;
    private String user_id;
    private String url;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.Recycleview);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        type = intent.getStringExtra("类型");
        if (type.equals("电影评论")){
            url = "http://132.232.78.106:8001/api/getMyNewsComment/";
        }else if (type.equals("新闻评论")){
            url = "http://132.232.78.106:8001/api/getMyFirmComment/";
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
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();

                    SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                    String session = preferences.getString("session","");
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();

                    String responseDate = response.body().string();
                    showResponse(responseDate);

                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(User_comments.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(User_comments.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(User_comments.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
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
            String state = jsonObject.getString("state");
            if (state.equals("1")){
                String result = jsonObject.getString("result");
//                Intent intent = getIntent();
//                user_id = intent.getStringExtra("user_id");
//                type = intent.getStringExtra("类型");
                if (type.equals("电影评论")){
                    movComs = gson.fromJson(result, new TypeToken<List<UserMovCom>>(){}.getType());
                    runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                        @Override
                        public void run(){
                            //设置ui
                            LinearLayoutManager manager=new LinearLayoutManager(User_comments.this);
                            recyclerView.setLayoutManager(manager);
                            userMovComAdapter = new UserMovComAdapter(movComs,user_id,User_comments.this);
                            recyclerView.setAdapter(userMovComAdapter);
                        }
                    });
                }else if (type.equals("新闻评论")){
                    newsComs = gson.fromJson(result, new TypeToken<List<UserNewsCom>>(){}.getType());
                    runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                        @Override
                        public void run(){
                            LinearLayoutManager manager=new LinearLayoutManager(User_comments.this);
                            recyclerView.setLayoutManager(manager);
                    userNewsComAdapter = new UserNewsComAdapter(newsComs,user_id,User_comments.this);
                    recyclerView.setAdapter(userNewsComAdapter);
                        }
                    });
                }

            }else {
                final String msg = jsonObject.getString("msg");
                runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                    @Override
                    public void run(){
                Toast.makeText(User_comments.this,msg,Toast.LENGTH_SHORT);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
