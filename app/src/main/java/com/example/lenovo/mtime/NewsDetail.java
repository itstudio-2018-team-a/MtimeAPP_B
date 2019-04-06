package com.example.lenovo.mtime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsDetail extends AppCompatActivity {

    String newsId;
    String user_id;
    FloatingActionButton fab;
    TextView tv_author;
    TextView tv_newsTitle;
    TextView tv_pubTime;
    ImageView iv_photo;
    TextView tv_content;

    int id;
    String author;
    String photo;
    String Time;
    String Title;
    int replyNum;
    String content;
    boolean isGood;
    Bitmap bitmap;
    String replys;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        newsId = intent.getStringExtra("newsId");
        user_id = intent.getStringExtra("user_id");

        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_newsTitle = (TextView) findViewById(R.id.tv_newsTitle);
        tv_pubTime = (TextView) findViewById(R.id.tv_pubTime);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);

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
                intent.putExtra("replys",replys);
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
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",newsId)
                            .add("operaType","1")
                            .add("session","9Mb5B9P7o7pb5tEBTAYNQsnDm6hMfI")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointNews/")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);

                    JSONObject jsonObject = new JSONObject(responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");

                    parseJSONWithGSON(result);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void parseJSONWithGSON(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            id = jsonObject.getInt("id");
            author = jsonObject.getString("author");
            photo = jsonObject.getString("photo");
            Time = jsonObject.getString("Time");
            Title = jsonObject.getString("Title");
            replyNum = jsonObject.getInt("replyNum");
            content = jsonObject.getString("content");
            isGood = jsonObject.getBoolean("isGood");
            replys = jsonObject.getJSONArray("replys").toString();
            bitmap = getHttpBitmap("http://132.232.78.106:8001/media/"+photo);
            Log.d("hhh",content);
            showResponse();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_author.setText(author);
                tv_content.setText(content);
                tv_newsTitle.setText(Title);
                tv_pubTime.setText(Time);
                iv_photo.setImageBitmap(bitmap);
            }
        });
    }

}
