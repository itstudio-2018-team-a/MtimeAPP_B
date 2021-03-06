package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MakeLongCom extends AppCompatActivity {

    Button btn_publish;
    EditText ed_title;
    EditText ed_content;
    EditText ed_subTitle;

    String movie_id;
    String user_id;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_long_com);

        btn_publish = (Button) findViewById(R.id.btn_publish);
        ed_content = (EditText) findViewById(R.id.ed_content);
        ed_title = (EditText) findViewById(R.id.ed_title);
        ed_subTitle = (EditText) findViewById(R.id.ed_subTitle);
        ed_subTitle.setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });
        ed_title.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
        ed_content.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2000) });

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
        movie_id = intent.getStringExtra("movie_id");
        user_id = intent.getStringExtra("user_id");
        session = intent.getStringExtra("session");
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        session = sharedPreferences.getString("session","");

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_content.getText().toString().equals("") || ed_subTitle.getText().toString().equals("")||ed_title.getText().toString().equals(""))
                    Toast.makeText(MakeLongCom.this,"以上内容不能为空！",Toast.LENGTH_SHORT).show();
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
                            .add("id",movie_id)
                            .add("content",ed_content.getText().toString())
                            .add("title",ed_title.getText().toString())
                            .add("subtitle",ed_subTitle.getText().toString())
                            .add("thumbnail","lalala")
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
                        Intent intent = new Intent(MakeLongCom.this,Movie_Details_Activity.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("movie_id",movie_id);
                        intent.putExtra("session",session);
                        finish();
                        startActivity(intent);
                        Toast.makeText(MakeLongCom.this,"发表成功",Toast.LENGTH_SHORT).show();
                    }
                    else if(state == -1)
                        Toast.makeText(MakeLongCom.this,"您还没有登录，不能发表评论",Toast.LENGTH_SHORT).show();
                    else if(state == -2)
                        Toast.makeText(MakeLongCom.this,"当前帖子不存在",Toast.LENGTH_SHORT).show();
                    else if(state == -3)
                        Toast.makeText(MakeLongCom.this,"啊哦，出错啦",Toast.LENGTH_SHORT).show();
                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(MakeLongCom.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(MakeLongCom.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(MakeLongCom.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
