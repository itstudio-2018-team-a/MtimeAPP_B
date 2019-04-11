package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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

public class MarkActivity extends AppCompatActivity {

    Button btn_shortComment;
    Button btn_longComment;
    RatingBar rb_mark;
    TextView tv_mark;
    Button btn_publish;

    String user_id;
    String movie_id;
    String mark;
    private String session;
    String score;
    Boolean isMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        btn_longComment = (Button) findViewById(R.id.btn_longComment);
        btn_shortComment = (Button) findViewById(R.id.btn_shortComment);
        tv_mark = (TextView) findViewById(R.id.tv_mark);
        btn_publish = (Button) findViewById(R.id.btn_publish);

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
        movie_id = intent.getStringExtra("movie_id");
//        session = intent.getStringExtra("session");
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        session = sharedPreferences.getString("session","");
        isMark = intent.getBooleanExtra("isMark",false);

        btn_shortComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MarkActivity.this,MakeShortCom.class);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("movie_id",movie_id);
                intent1.putExtra("session",session);
                finish();
                startActivity(intent1);
            }
        });

        btn_longComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MarkActivity.this,MakeLongCom.class);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("movie_id",movie_id);
                intent1.putExtra("session",session);
                finish();
                startActivity(intent1);
            }
        });
        rb_mark = (RatingBar) findViewById(R.id.rb_mark);
        rb_mark.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(isMark) tv_mark.setText("已评");
                else tv_mark.setText(String.valueOf(rating));
                if(fromUser) mark = String.valueOf(rating);
                else mark = "0";
            }
        });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMark) Toast.makeText(MarkActivity.this,"当前电影您已评分",Toast.LENGTH_SHORT).show();
                else {
                    score = tv_mark.getText().toString();
                    if (!score.equals("")){
                        score = score.substring(0,score.lastIndexOf('.'));
                        sendRequestWithOkHttp();
                    }

                }
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
                            .add("score",score)
                            .add("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/scorePointFilm/")   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();

                    String responseDate = response.body().string();
                    Log.d("mark返回数据",responseDate);
                    JSONTokener(responseDate);
                    JSONObject jsonObject = new JSONObject(JSONTokener(responseDate));
                    int state = jsonObject.getInt("state");
                    String msg = jsonObject.getString("msg");
                    Looper.prepare();
                    if(state==1)
                    {
                        Toast.makeText(MarkActivity.this,"评分成功",Toast.LENGTH_LONG).show();
                    }
                    else if(state == -1)
                        Toast.makeText(MarkActivity.this,"您还没有登录，不能评分",Toast.LENGTH_LONG).show();
                    else if(state == -2)
                        Toast.makeText(MarkActivity.this,"当前内容不存在",Toast.LENGTH_LONG).show();
                    else if(state == -3)
                        Toast.makeText(MarkActivity.this,"啊哦，出错啦",Toast.LENGTH_LONG).show();
                    else if(state == -4)
                        Toast.makeText(MarkActivity.this,"您已经评过分了",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(MarkActivity.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(MarkActivity.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(MarkActivity.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
}
