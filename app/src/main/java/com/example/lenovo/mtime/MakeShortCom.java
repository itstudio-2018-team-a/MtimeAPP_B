package com.example.lenovo.mtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MakeShortCom extends AppCompatActivity {

    EditText ed_comments;
    Button btn_publish;

    String user_id;
    String movie_id;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_short_com);

        Intent intent = getIntent();
        movie_id = intent.getStringExtra("movie_id");
        user_id = intent.getStringExtra("user_id");
        session = intent.getStringExtra("session");

        ed_comments = (EditText) findViewById(R.id.ed_content);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        ed_comments.setFilters(new InputFilter[] { new InputFilter.LengthFilter(200) });

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            .add("id",movie_id)
                            .add("content",ed_comments.getText().toString())
                            .add("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/reviewPointFilm/")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");
                    if(state==1)
                    {
                        Intent intent = new Intent(MakeShortCom.this,Movie_Details_Activity.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("movie_id",movie_id);
                        intent.putExtra("session",session);
                        startActivity(intent);
                        Toast.makeText(MakeShortCom.this,"发表成功",Toast.LENGTH_LONG).show();
                    }
                    else if(state == -1)
                        Toast.makeText(MakeShortCom.this,"您还没有登录，不能发表评论",Toast.LENGTH_LONG).show();
                    else if(state == -2)
                        Toast.makeText(MakeShortCom.this,"当前帖子不存在",Toast.LENGTH_LONG).show();
                    else if(state == -3)
                        Toast.makeText(MakeShortCom.this,"啊哦，出错啦",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
