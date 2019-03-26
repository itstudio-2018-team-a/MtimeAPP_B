package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button button_Login;
    private EditText ed_account;
    private EditText ed_password;
    private String user_id;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        button_Login = findViewById(R.id.btn_login);
        ed_account = findViewById(R.id.ed_account);
        ed_password = findViewById(R.id.ed_password);

        button_Login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                user_id = ed_account.getText().toString();
                password = ed_password.getText().toString();
                sendRequestWithOkHttp();
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
                            .add("password",password)
                            .build();

                    //从sharedpreferences获取cookie
                    SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
                    String cookie = preferences.getString("cookie","");

                    Log.d("cookie",cookie);

                    Request request = new Request.Builder()
                            .url("http://106.13.106.1/to_post")   //网址有待改动
                            .post(requestBody)
                            .addHeader("cookie",cookie)
                            .build();

                    Response response = client.newCall(request).execute();
                    cookie = response.header("Set-Cookie");  //获取cookie

                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("cookie",cookie);

                    String responseDate = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseDate);
                    String result = jsonObject.getString("result");
                    if (result.equals("0")){
                        Toast.makeText(Login_Activity.this,"注册成功",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if (result.equals("1")){
                        Toast.makeText(Login_Activity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    }else if (result.equals("2")){
                        Toast.makeText(Login_Activity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    }else if (result.equals("4")){
                        Toast.makeText(Login_Activity.this,"该账号已被冻结",Toast.LENGTH_LONG).show();
                    }else if (result.equals("5")){
                        Toast.makeText(Login_Activity.this,"无效的密码，请修改后再试",Toast.LENGTH_LONG).show();
                    }else if (result.equals("6")){
                        Toast.makeText(Login_Activity.this,"未知错误导致登陆失败，请稍后再试",Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
