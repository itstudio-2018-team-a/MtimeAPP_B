package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.adapter.CommentsAdapter;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.adapter.MyMovComAdapter;
import com.example.lenovo.mtime.adapter.MyNewsComAdapter;
import com.example.lenovo.mtime.adapter.UserMovComAdapter;
import com.example.lenovo.mtime.adapter.UserNewsComAdapter;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.MyMovCom;
import com.example.lenovo.mtime.bean.MyNewsCom;
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
    private List<MyMovCom> myMovComs = new ArrayList<>();
    private List<MyNewsCom> myNewsComs = new ArrayList<>();
    private MyNewsComAdapter myNewsComAdapter;
    private MyMovComAdapter myMovComAdapter;
    private String user_id;
    private String url;
    private String type;
    private String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView = findViewById(R.id.Recycleview);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        type = intent.getStringExtra("类型");
        if (type.equals("电影评论")){
            url = "http://132.232.78.106:8001/api/getMyFirmComment/";
        }else if (type.equals("新闻评论")){
            url = "http://132.232.78.106:8001/api/getMyNewsComment/";
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
                    myMovComs = gson.fromJson(result, new TypeToken<List<MyMovCom>>(){}.getType());
                    runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                        @Override
                        public void run(){
                            //设置ui
                            LinearLayoutManager manager=new LinearLayoutManager(User_comments.this);
                            recyclerView.setLayoutManager(manager);
                            myMovComAdapter = new MyMovComAdapter(myMovComs,user_id,User_comments.this,session);
                            recyclerView.setAdapter(myMovComAdapter);
                        }
                    });
                }else if (type.equals("新闻评论")){
                    myNewsComs = gson.fromJson(result, new TypeToken<List<MyNewsCom>>(){}.getType());
                    runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                        @Override
                        public void run(){
                            LinearLayoutManager manager=new LinearLayoutManager(User_comments.this);
                            recyclerView.setLayoutManager(manager);
                            myNewsComAdapter = new MyNewsComAdapter(myNewsComs,user_id,User_comments.this,session);
                            recyclerView.setAdapter(myNewsComAdapter);
                        }
                    });
                }

            }else if(state.equals("-1")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(User_comments.this,Login_Activity.class);
                        startActivity(intent);
                        Login_Activity.flag = "0";
                        finish();

                        //还没写完
                        Toast.makeText(User_comments.this,"登陆已过期，请重新登录",Toast.LENGTH_SHORT).show();
                    }
                });
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
