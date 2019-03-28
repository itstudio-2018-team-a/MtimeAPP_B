package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.bean.User;
import com.example.lenovo.mtime.fragment.UserFragment;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeName extends AppCompatActivity {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        EditText et_newName = findViewById(R.id.et_newName);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String user_id;
        Intent intent = getIntent();
        //获取到修改后的用户名以便发送到服务器
        String newName = et_newName.getText().toString();
        user_id = intent.getStringExtra("user_id");
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                url = "106.13.106.1/account/i/user/info/" + user_id;
                sendRequestWithOkHttp();  //发起网络请求从服务器获取相关用户数据

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
                    Request request = new Request.Builder()
                            .url(url)   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String cookie = response.header("Set-Cookie");  //获取cookie

                    //将cookie储存到sharedpreference
                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("cookie",cookie);
                    editor.apply();

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
        final User user = gson.fromJson(response,User.class);

        runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui

//                Glide.with(getContext()) //显示头像
//                        .load(user.getHeadImage_url())
//                        .placeholder(R.drawable.logo)
//                        .error(R.drawable.logo)
//                        .into(user_image);
//
//                tv_userName.setText(user.getUser_Name());
            }
        });
    }
}
