package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.uitl.SpaceFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
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
    private String result;
    private CheckBox checkBox;
    private String msg;
    private String session;
    private String nickName;
    private String headImage;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        button_Login = findViewById(R.id.btn_login);
        button_register = findViewById(R.id.btn_register);
        ed_account = findViewById(R.id.ed_account);
        ed_password = findViewById(R.id.ed_password);
        checkBox = findViewById(R.id.remember_password);

        //禁止输入空格
        ed_account.setFilters(new InputFilter[]{new SpaceFilter()});
        ed_password.setFilters(new InputFilter[]{new SpaceFilter()});
        //限制输入最大长度
        ed_account.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});
        ed_password.setFilters( new InputFilter[]{new InputFilter.LengthFilter(16)});

        button_Login.setOnClickListener(this);
        button_register.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                user_id = ed_account.getText().toString();
                password = ed_password.getText().toString();
                int len_id = user_id.length();
                int len_pass = password.length();
                if (len_id<6){
                    Toast.makeText(this,"请输入6-16位账号",Toast.LENGTH_SHORT).show();
                }else {
                    if (len_pass<6){
                        Toast.makeText(this,"请输入6-16位密码",Toast.LENGTH_SHORT).show();
                    }else {

                        sendRequestWithOkHttp();
                    }
                }

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

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username",user_id)
                            .addFormDataPart("password",password)
                            .build();

                    //从sharedpreferences获取cookie
//                    SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
//                    String cookie = preferences.getString("cookie","");

//                    Log.d("cookie",cookie);

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/login/")   //网址有待改动
                            .post(requestBody)
                            .addHeader("Connection","close")
//                            .addHeader("cookie",cookie)
                            .build();

                    Call call = client.newCall(request);

                    try{
                        Response response = call.execute();
                        String responseDate = response.body().string();

//                    cookie = response.header("Set-Cookie");  //获取cookie
//                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//                    editor.putString("cookie",cookie);

                    JSONArray jsonArray = new JSONArray(responseDate);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    result = jsonObject.getString("statu");

                    if(result.equals("1")){ //如果登陆成功，则读取以下数据
                        session = jsonObject.getString("session");
                        nickName = jsonObject.getString("nickName");
                        user_id = jsonObject.getString("username");
                        headImage = jsonObject.getString("headImage");
                        email = jsonObject.getString("email");

                    }else {  //若登陆失败则toast失败原因
                        msg = jsonObject.getString("msg");
                    }
                    showResult();


                }catch (Exception e){
                    e.printStackTrace();
                    if (e instanceof SocketTimeoutException){
                        Toast.makeText(Login_Activity.this,"连接超时",Toast.LENGTH_SHORT).show();
                    }
                    if (e instanceof ConnectException){
                        Toast.makeText(Login_Activity.this,"连接异常",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    void showResult(){
        runOnUiThread(new Runnable() {

            private SharedPreferences.Editor editor;

            @Override
            public void run() {
                if (result.equals("1")){

                    if (checkBox.isChecked()){
                        editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putString("user_id",user_id);
                        editor.putString("password",password);
                        editor.putString("session",session);
                        editor.apply();
                    }else {
                        editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.clear();
                    }
                    Toast.makeText(Login_Activity.this,"登录成功",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login_Activity.this,MainActivity.class);
                    intent.putExtra("user_id",user_id);
                    intent.putExtra("session",session);
                    intent.putExtra("nickName",nickName);
                    intent.putExtra("headImage",headImage);
                    intent.putExtra("email",email);
                    flag = "1" ; //用于个人中心判断是否以及登录
                    startActivity(intent);
                    finish();
                }else {
                            Toast.makeText(Login_Activity.this,msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_findpassword, menu);
        final MenuItem item = menu.findItem(R.id.btn_findPassword);
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
            case R.id.btn_findPassword:
                Intent intent = new Intent(Login_Activity.this, FindPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
