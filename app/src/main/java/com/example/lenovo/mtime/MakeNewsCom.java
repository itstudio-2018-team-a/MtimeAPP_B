package com.example.lenovo.mtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_news_com);

        Intent intent = new Intent();
        user_id = intent.getStringExtra("user_id");

        btn_publish = (Button) findViewById(R.id.btn_publish);
        et_comments = (EditText) findViewById(R.id.et_comments);
        et_comments.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1000) });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = et_comments.getText().toString();
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
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id",user_id)
                            .add("content",comments)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://106.13.106.1/news/i/post/new_comment")   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseDate);
                    String result = jsonObject.getString("result");
                    if(request.equals("0"))
                    {
                        Intent intent = new Intent(MakeNewsCom.this,NewsDetail.class);
                        startActivity(intent);
                        Toast.makeText(MakeNewsCom.this,"发表成功",Toast.LENGTH_LONG).show();
                    }
                    else if(request.equals("1"))
                        Toast.makeText(MakeNewsCom.this,"验证码无效",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
