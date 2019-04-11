package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.uitl.SpaceFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.lenovo.mtime.Login_Activity.flag;
import static com.example.lenovo.mtime.uitl.SpaceFilter.setEditTextInhibitInputSpace;

public class register_Infor extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_account;
    private EditText ed_password;
    private EditText ed_email;
    private EditText ed_code;
    private EditText ed_password_sure;
    private Button btn_register;
    private Button btn_get_code;
    private String user_id;
    private String password;
    private String password_sure;
    private String code;
    private String email;
    private int flag = 0;  //用于判断获取验证码按钮是否按下
    private String session;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_infor);

        ed_account = findViewById(R.id.ed_account);
        ed_password = findViewById(R.id.ed_password);
        ed_email = findViewById(R.id.ed_email);
        ed_code = findViewById(R.id.ed_code);
        ed_password_sure = findViewById(R.id.ed_password_sure);

        //禁止输入空格
        setEditTextInhibitInputSpace(ed_account);
        setEditTextInhibitInputSpace(ed_password);
        setEditTextInhibitInputSpace(ed_email);
        setEditTextInhibitInputSpace(ed_code);
        setEditTextInhibitInputSpace(ed_password_sure);
//        ed_account.setFilters(new InputFilter[]{new SpaceFilter()});
//        ed_password.setFilters(new InputFilter[]{new SpaceFilter()});
//        ed_email.setFilters(new InputFilter[]{new SpaceFilter()});
//        ed_code.setFilters(new InputFilter[]{new SpaceFilter()});
//        ed_password_sure.setFilters(new InputFilter[]{new SpaceFilter()});
         //限制最大输入长度
        ed_account.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});
        ed_password.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});
        ed_email.setFilters( new InputFilter[]{new InputFilter.LengthFilter(30)});
        ed_code.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});
        ed_password_sure.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});

        btn_register = findViewById(R.id.btn_register);
        btn_get_code = findViewById(R.id.btn_get_code);

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


        btn_register.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:

                if (flag == 0){
                    Toast.makeText(this,"请先获取验证码",Toast.LENGTH_SHORT).show();
                }else {
                    //按下按键首先获取输入框中的内容，再发起网络请求进行post

                    user_id = ed_account.getText().toString();
                    password = ed_password.getText().toString();
                    password_sure = ed_password_sure.getText().toString();
                    code = ed_code.getText().toString();
                    email = ed_email.getText().toString();

                    int len_id = user_id.length();
                    int len_pass = user_id.length();

                    if (len_id<6){
                        Toast.makeText(this,"请输入6-16位账号",Toast.LENGTH_SHORT).show();
                    }else if (len_pass<6){
                        Toast.makeText(this,"请输入6-16位密码",Toast.LENGTH_SHORT).show();
                    }else if (!password_sure.equals(password) ){
                        Toast.makeText(this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    }else if ( code.equals("") ){
                        Toast.makeText(this,"请输入验证码",Toast.LENGTH_SHORT).show();
                    }else if ( email.equals("") ){
                        Toast.makeText(this,"请输入邮箱",Toast.LENGTH_SHORT).show();
                    }else {
                        sendRequestWithOkHttp();
                    }
                }
                break;

            case R.id.btn_get_code:
                email = ed_email.getText().toString();
                if (email.equals("")){

                    Toast.makeText(this,"请输入邮箱地址",Toast.LENGTH_SHORT).show();
                }else {
                sendRequestWithOkHttp_getCode();
                flag = 1;
                }
                break;
                default:
                    break;

        }
    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username",user_id)
                            .addFormDataPart("nickname","陆仁甲")
                            .addFormDataPart("password",password)
                            .addFormDataPart("vericode",code)
                            .addFormDataPart("email",email)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/register/")
                            .post(requestBody)
                            .addHeader("Connection","close")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
//                    session = response.header("Set-Cookie");  //获取cookie



                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);
                    final JSONObject jsonObject = new JSONObject(responseDate);

                    final String result = jsonObject.getString("state");


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("1")){
                                Toast.makeText(register_Infor.this,"注册成功",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(register_Infor.this,Login_Activity.class);
                                //把session储存到本地
                                try {
                                    session = jsonObject.getString("session");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                                editor.putString("cookie",session);

                                startActivity(intent);
                                finish();
                            }
                            else if (result.equals("-1")){
                                Toast.makeText(register_Infor.this,"很抱歉，该验证码已过期",Toast.LENGTH_LONG).show();
                            }else if (result.equals("-2")){
                                Toast.makeText(register_Infor.this,"很抱歉，验证码错误",Toast.LENGTH_LONG).show();
                            }else if (result.equals("404")){
                                Toast.makeText(register_Infor.this,"非法请求导致注册失败",Toast.LENGTH_LONG).show();
                            }

                        }
                    });


                }catch (final Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(register_Infor.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(register_Infor.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(register_Infor.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }



    private void sendRequestWithOkHttp_getCode(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){     //此线程用于获取验证码
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("email",email)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/sendCheckCode/")
                            .post(requestBody)
                            .addHeader("Connection","close")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONTokener(responseDate);
                    JSONArray jsonArray = new JSONArray(responseDate);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    final String statu = jsonObject.getString("statu");
                    String msg = jsonObject.getString("msg");

                    runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
                        @Override
                        public void run(){
                            //设置ui

                            if (statu.equals("1")){
                                Toast.makeText(register_Infor.this,"验证码已发送，请注意查收",Toast.LENGTH_LONG).show();
                            }else if (statu.equals("-1")) {
                                Toast.makeText(register_Infor.this, "该邮箱已被注册", Toast.LENGTH_LONG).show();
                            }

                        }
                    });


                    //这里后期考虑在界面动态显示验证码发送时间倒计时

                }catch (Exception e){
                    e.printStackTrace();

                    sendRequestWithOkHttp_getCode();
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
