package com.example.lenovo.mtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.example.lenovo.mtime.uitl.okhttp_plus.sendRequestwithOkHttp;

public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    EditText User_id;
    EditText Email;
    EditText newPassword;
    EditText repeatPassword;
    EditText Code;
    Button btn_sendCode;
    Button btn_Reset;
    Button btn_Cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        User_id = findViewById(R.id.et_UserId);
        Email = findViewById(R.id.et_email);
        newPassword = findViewById(R.id.et_newPassword);
        repeatPassword = findViewById(R.id.et_repeatPassword);
        Code = findViewById(R.id.et_code);
        btn_sendCode = findViewById(R.id.send_code);
        btn_Reset = findViewById(R.id.btn_reset);
        btn_Cancel = findViewById(R.id.btn_out);

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

        btn_sendCode.setOnClickListener(this);
        btn_Reset.setOnClickListener(this);
        btn_Cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String user_id = User_id.getText().toString();
        String email = Email.getText().toString();
        String password = newPassword.getText().toString();
        String repead_password = repeatPassword.getText().toString();
        String code = Code.getText().toString();

        switch (v.getId()) {
            case R.id.send_code:

                if (email.equals("")) {
                    Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
                } else {

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username", user_id)
                            .addFormDataPart("email", email)
                            .build();
                    String ResponseData = sendRequestwithOkHttp("http://132.232.78.106:8001/api/LookForPwd/", requestBody, user_id, this);
                    if(ResponseData == null){
                        Toast.makeText(this,"未知错误，请稍后再试",Toast.LENGTH_SHORT).show();
                    }else showResponse(ResponseData);
                }
                break;

            case R.id.btn_reset:
                code = Code.getText().toString();
                if (user_id.equals("")) {
                    Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(repead_password)) {
                    Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                } else if (code.equals("")) {
                    Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                }else {
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username",user_id)
                            .addFormDataPart("code",code)
                            .addFormDataPart("newPwd",password)
                            .build();
                    String ResponseData = sendRequestwithOkHttp("http://132.232.78.106:8001/api/changePwd/",requestBody,user_id,this);
                    showResponse(ResponseData);
                }
                break;
            case R.id.btn_out:
                onBackPressed();
                break;
            default:
                break;

        }
    }


    public void showResponse(final String ResponseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray(ResponseData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int statu = jsonObject.getInt("statu");
                    String msg = jsonObject.getString("msg");
                    Toast.makeText(FindPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
