package com.example.lenovo.mtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.uitl.SpaceFilter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.lenovo.mtime.Login_Activity.flag;

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
        ed_account.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_password.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_email.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_code.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_password_sure.setFilters(new InputFilter[]{new SpaceFilter()});

        btn_register = findViewById(R.id.btn_register);
        btn_get_code = findViewById(R.id.btn_get_code);




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

                    if (user_id.equals("") ){
                        Toast.makeText(this,"请输入账号",Toast.LENGTH_SHORT).show();
                    }else if (password.equals("") ){
                        Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
                    }else if (password_sure.equals("") ){
                        Toast.makeText(this,"请确认密码",Toast.LENGTH_SHORT).show();
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
                    RequestBody requestBody = new FormBody.Builder()
                            .add("user_id",user_id)
                            .add("user_name","某不知名网友")
                            .add("password",password)
                            .add("verify_id",code)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://39.96.208.176/account/i/register")   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);
                    JSONObject jsonObject = new JSONObject(responseDate);

                    String result = jsonObject.getString("result");
                    if (result.equals("0")){
                        Toast.makeText(register_Infor.this,"注册成功",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(register_Infor.this,Login_Activity.class);
                        startActivity(intent);
                        finish();
                    }else if (result.equals("1")){
                        Toast.makeText(register_Infor.this,"很抱歉，该账号已存在",Toast.LENGTH_LONG).show();
                    }else if (result.equals("2")){
                        Toast.makeText(register_Infor.this,"很抱歉，该邮箱已被注册",Toast.LENGTH_LONG).show();
                    }else if (result.equals("3")){
                        Toast.makeText(register_Infor.this,"验证码错误",Toast.LENGTH_LONG).show();
                    }else if (result.equals("4")){
                        Toast.makeText(register_Infor.this,"无效的用户名，请修改后再试",Toast.LENGTH_LONG).show();
                    }else if (result.equals("5")){
                        Toast.makeText(register_Infor.this,"无效的密码，请修改后再试",Toast.LENGTH_LONG).show();
                    }else if (result.equals("6")){
                        Toast.makeText(register_Infor.this,"未知错误导致注册失败，请稍后再试",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
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
                            .add("use","register")
                            .add("email",email)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://39.96.208.176/i/email_verify_code")   //网址有待改动
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONTokener(responseDate);
                    JSONObject jsonObject = new JSONObject(responseDate);
                    String id = jsonObject.getString("id");
                    String wait = jsonObject.getString("wait");
                    Toast.makeText(register_Infor.this,"验证码已发送，请注意查收",Toast.LENGTH_LONG).show();

                    //这里后期考虑在界面动态显示验证码发送时间倒计时

                }catch (Exception e){
                    e.printStackTrace();
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
