package com.example.lenovo.mtime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

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

public class CommentsDetail extends AppCompatActivity {

    String commentsId;
    String session;

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

    TextView tv_title;
    TextView tv_content;
    TextView tv_author;
    TextView tv_movieTitle;
    ImageView iv_author;
    ImageView iv_movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_detail);

        Intent intent = new Intent();
        commentsId = intent.getStringExtra("commentsId");

        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_movieTitle=(TextView) findViewById(R.id.tv_movieTitle);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_author = (ImageView) findViewById(R.id.iv_author);
        iv_movie = (ImageView) findViewById(R.id.iv_movie);


        sendRequestWithOkHttp();
    }
    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",commentsId)
                            .add("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointFilmReview/")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");

                    parseJSONWithGSON(result);


                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);

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
            bitmap = getHttpBitmap(photo);
            Log.d("hhh",content);
            showResponse();
            //newsList = gson.fromJson(list, new TypeToken<List<News>>(){}.getType());

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
                tv_content.setText(content);
                tv_author.setText(author);
                tv_title.setText(Title);

            }
        });
    }

}
