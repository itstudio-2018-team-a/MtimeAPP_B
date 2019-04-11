package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeNewsCom extends AppCompatActivity {
    EditText et_comments;
    Button btn_publish;
    String comments;
    String user_id;
    String newsId;
    String session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_news_com);

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
        session = intent.getStringExtra("session");
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        session = sharedPreferences.getString("session","");


        btn_publish = (Button) findViewById(R.id.btn_publish);
        et_comments = (EditText) findViewById(R.id.et_comments);
        et_comments.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = et_comments.getText().toString();
                if (comments.equals("")) Toast.makeText(MakeNewsCom.this,"评论内容不能为空！",Toast.LENGTH_SHORT).show();
                else
                    sendRequestWithOkHttp();
            }
        });
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
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",newsId)
                            .add("content",comments)
                            .add("operaType","1")
                            .add("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/replyPointNews/")   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");
                    Looper.prepare();
                    if(state==1)
                    {
                        Intent intent = new Intent(MakeNewsCom.this,NewsDetail.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("session",session);
                        intent.putExtra("newsId",newsId);
                        finish();
                        startActivity(intent);
                        Toast.makeText(MakeNewsCom.this,"发表成功",Toast.LENGTH_LONG).show();
                    }
                    else if(state == -1)
                        Toast.makeText(MakeNewsCom.this,"您还没有登录，不能发表评论",Toast.LENGTH_LONG).show();
                    else if(state == -2)
                        Toast.makeText(MakeNewsCom.this,"当前帖子不存在",Toast.LENGTH_LONG).show();
                    else if(state == -3)
                        Toast.makeText(MakeNewsCom.this,"啊哦，出错啦",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(MakeNewsCom.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(MakeNewsCom.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(MakeNewsCom.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
