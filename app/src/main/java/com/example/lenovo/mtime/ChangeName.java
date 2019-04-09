package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.bean.User;
import com.example.lenovo.mtime.fragment.UserFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangeName extends AppCompatActivity {

    private String url;
    private String session;
    private String newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        EditText et_newName = findViewById(R.id.et_newName);
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String user_id;
        //获取到修改后的用户名以便发送到服务器
        newName = et_newName.getText().toString();
//        user_id = intent.getStringExtra("nickName");
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                url = "http://132.232.78.106:8001/api/changeNickName/";
                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                session = sharedPreferences.getString("session", "");
                sendRequestWithOkHttp(newName);  //发起网络请求从服务器获取相关用户数据

            }
        });
    }

    private void sendRequestWithOkHttp(final String name) {
        //开启现线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();


                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("nickName", name)
                            .addFormDataPart("session", session)
                            .build();
                    Request request = new Request.Builder()
                            .post(requestBody)
                            .url(url)   //网址有待改动
                            .build();

                    Call call = client.newCall(request);
                    String responseDate = "";

                    Response response = call.execute();
                    responseDate = response.body().string();
                    Log.d("ZGH", responseDate);


                    showResponse(responseDate);


                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof SocketTimeoutException) {
                        Toast.makeText(ChangeName.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                    if (e instanceof ConnectException) {
                        Toast.makeText(ChangeName.this, "连接异常", Toast.LENGTH_SHORT).show();
                    }
                    if (e instanceof ProtocolException) {
                        Toast.makeText(ChangeName.this, "未知异常，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String responseDate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseDate);
                    String state = jsonObject.getString("state");
                    String msg = jsonObject.getString("msg");
                    if (state.equals("1")) {
                        Toast.makeText(ChangeName.this, msg, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("nickName", newName);
                        editor.apply();
                    }else Toast.makeText(ChangeName.this, msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
