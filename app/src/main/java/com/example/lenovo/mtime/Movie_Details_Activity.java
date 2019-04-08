package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.adapter.MovieComAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.MovieCom;
import com.example.lenovo.mtime.bean.Movie_details;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Movie_Details_Activity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    String user_id;
    String movie_id;
    private String session;
    private Movie_details movie_details;
    private List<MovieCom> movieComList;
    MovieComAdapter movieComAdapter;
    private Context context;
    private MovieCom movieCom;

    ImageView imageView;
    TextView textView_title;
    TextView textView_time;
    TextView textView_releaseDate;
    TextView textView_mark;
    private String title;
    private String image;
    private String mark;
    private String relase_date;
    private String replys;
    private String displayTime;
    private String time;
    private String id;
    private String replyNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie__details);

        context = this;

        imageView = findViewById(R.id.iv_movieImg);
        textView_title = findViewById(R.id.tv_movieTitle);
        textView_time = findViewById(R.id.tv_time);
        textView_releaseDate = findViewById(R.id.tv_releaseDate);
        textView_mark = findViewById(R.id.tv_mark);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        final Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        movie_id = intent.getStringExtra("movie_id");
        session = intent.getStringExtra("session");

        sendRequestWithOkHttp();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id == null)
                {
                    Toast.makeText(Movie_Details_Activity.this, "您还没有登录，不能发表评论", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent1 = new Intent(Movie_Details_Activity.this,MarkActivity .class);
                    intent1.putExtra("user_id",user_id);
                    intent1.putExtra("movie_id",movie_id);
                    intent1.putExtra("session",session);
                    startActivity(intent1);
                }
            }
        });


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
                //Intent intent = new Intent(Movie_Details_Activity.this, .class);
                //intent.putExtra("user_id",user_id);
                //startActivity(intent);
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
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id",movie_id)
                            .addFormDataPart("session", session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointFilm/")
                            .addHeader("Connection","close")
                            .post(requestBody)
                            .build();

//
                    Response response = client.newCall(request).execute();

//
                    String responseDate = response.body().string();
                    Log.d("hahaha",responseDate);
                    showResponse(responseDate);

                }catch (Exception e){
                    e.printStackTrace();
                    sendRequestWithOkHttp();
                }
            }
        }).start();
    }

    private void showResponse(final String response){

        Gson gson = new Gson();
        try {
            final JSONObject jsonObject = new JSONObject(response);
            int state = jsonObject.getInt("state");
            if (state == 1) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("result");

                title = jsonObject1.getString("title");
                image = jsonObject1.getString("image");
                image = "http://132.232.78.106:8001"+ image;
                mark = jsonObject1.getString("mark");
                relase_date = jsonObject1.getString("relase_date");
                displayTime = jsonObject1.getString("displayTime");
                time = jsonObject1.getString("time");
                id = jsonObject1.getString("id");
                replyNum = jsonObject1.getString("replyNum");
                replys = jsonObject1.getJSONArray("replys").toString();
                parseJSONWithGSON(replys);
//                movie_details = gson.fromJson(list, Movie_details.class);

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        Glide.with(Movie_Details_Activity.this).load(image).placeholder(R.drawable.firstheadimage).error(R.drawable.user_128).into(imageView);
                        textView_mark.setText(mark);
                        textView_releaseDate.setText(relase_date);
                        textView_time.setText(time);
                        textView_title.setText(title);

                    }
                });
            }else {
                final String msg = jsonObject.getString("msg");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Movie_Details_Activity.this,msg,Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void parseJSONWithGSON(final String response){

        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(response);
            movieComList = gson.fromJson(response, new TypeToken<List<MovieCom>>(){}.getType());
            Log.d("listhhh",movieComList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);

                movieComAdapter = new MovieComAdapter(movieComList,user_id,context);

                recyclerView.setAdapter(movieComAdapter);
            }
        });
    }
}
