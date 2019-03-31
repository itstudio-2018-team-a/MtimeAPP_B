package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.uitl.SpaceFilter;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button button_Login;
    private Button button_register;
    private EditText ed_account;
    private EditText ed_password;
    private String user_id;
    private String password;
    public static String flag; //用于判断是否登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        button_Login = findViewById(R.id.btn_login);
        button_register = findViewById(R.id.btn_register);
        ed_account = findViewById(R.id.ed_account);
        ed_password = findViewById(R.id.ed_password);

        //禁止输入空格
        ed_account.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_password.setFilters(new InputFilter[]{new SpaceFilter()});

        button_Login.setOnClickListener(this);
        button_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                user_id = ed_account.getText().toString();
                password = ed_password.getText().toString();
                sendRequestWithOkHttp();
                break;

            case R.id.btn_register:
                Intent intent = new Intent(Login_Activity.this,register_Infor.class);
                startActivity(intent);
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
                            .url("http://39.96.208.176/to_post")   //网址有待改动
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
                        Toast.makeText(Login_Activity.this,"登录成功",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                        intent.putExtra("extra_data",user_id);
                        flag = "1" ; //用于个人中心判断是否以及登录
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
