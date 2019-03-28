package com.example.lenovo.mtime;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.bean.User;
import com.example.lenovo.mtime.fragment.UserFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePassword extends AppCompatActivity {
    public String realName;
    public String realPassword;
    private String old_password; //旧密码
    private String new_password; //新密码
    private String verify_id; //验证码id
    private String verify_code; //验证码值
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final EditText et_oldPassword = (EditText) findViewById(R.id.et_oldPassword);
        final EditText et_newPassword = (EditText) findViewById(R.id.et_newPassword);
        final EditText et_repeatPassword = (EditText) findViewById(R.id.et_repeatPassword);
        et_oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_repeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_newPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        et_repeatPassword.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String user_id;
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");



        setEditTextInhibitInputSpace(et_newPassword);
        setEditTextInhibitInputSpace(et_oldPassword);
        setEditTextInhibitInputSpace(et_repeatPassword);

        old_password = et_oldPassword.getText().toString();
        new_password = et_newPassword.getText().toString();
        //验证码获取有待修改
        verify_id = et_newPassword.getText().toString();
        verify_code = et_newPassword.getText().toString();


        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "106.13.106.1/account/i/changepasswd/" + user_id;
                sendRequestWithOkHttp();  //发起网络请求从服务器获取相关用户数据
            }
        });
    }
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))
                    return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
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

    private void showResponse(final String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            String result = jsonObject.getString("result");
            if (result.equals("0")){
                Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }else if (result.equals("1")){
                Toast.makeText(this,"旧密码错误，请修正后再试",Toast.LENGTH_LONG).show();

            }else if (result.equals("2")){
                Toast.makeText(this,"验证码错误，请修正后再试",Toast.LENGTH_LONG).show();

            }else if (result.equals("3")){
                Toast.makeText(this,"请先登录",Toast.LENGTH_LONG).show();

            }else if (result.equals("4")){
                Toast.makeText(this,"出现未知错误，请稍后再试",Toast.LENGTH_LONG).show();

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
