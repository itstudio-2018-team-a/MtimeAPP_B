package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Movie_Details_Activity extends AppCompatActivity {
    FloatingActionButton fab;
    String user_id;
    String movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie__details);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        movie_id = intent.getStringExtra("movie_id");

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

                    SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                    String session = preferences.getString("session","");

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username",user_id)
                            .addFormDataPart("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointFilm/")
                            .addHeader("Connection","close")
                            .build();

//
                    Response response = client.newCall(request).execute();
//
                    String responseDate = response.body().string();
                    showResponse(responseDate);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int state = jsonObject.getInt("state");
            String list = jsonObject.getString("result");
//            String status = jsonObject.getString("status");




        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                //设置ui
//                LinearLayoutManager manager=new LinearLayoutManager(getContext());
//                recyclerView.setLayoutManager(manager);
//                movieAdapter = new MovieAdapter(getContext(), movies,user_id);
//                recyclerView.setAdapter(movieAdapter);
            }
        });
    }
}
