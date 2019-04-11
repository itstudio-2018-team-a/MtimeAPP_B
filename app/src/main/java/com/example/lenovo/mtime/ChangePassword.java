package com.example.lenovo.mtime;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.mtime.bean.User;
import com.example.lenovo.mtime.fragment.UserFragment;
import com.example.lenovo.mtime.uitl.okhttp_plus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.lenovo.mtime.uitl.SpaceFilter.setEditTextInhibitInputSpace;

public class ChangePassword extends AppCompatActivity {
    public String realName;
    public String realPassword;
    private String old_password; //旧密码
    private String new_password; //新密码
    private String new_password_sure;
    private String url;
    private String user_id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        setEditTextInhibitInputSpace(et_oldPassword);
        setEditTextInhibitInputSpace(et_newPassword);
        setEditTextInhibitInputSpace(et_repeatPassword);
        et_oldPassword.setFilters( new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_newPassword.setFilters( new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_repeatPassword.setFilters( new InputFilter[]{new InputFilter.LengthFilter(10)});

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


        setEditTextInhibitInputSpace(et_newPassword);
        setEditTextInhibitInputSpace(et_oldPassword);
        setEditTextInhibitInputSpace(et_repeatPassword);



        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_password = et_oldPassword.getText().toString();
                new_password = et_newPassword.getText().toString();
                new_password_sure = et_repeatPassword.getText().toString();
                if (new_password.equals(new_password_sure)){
                    SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                    String session = sharedPreferences.getString("session","");
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("oldPassword",old_password)
                            .addFormDataPart("newPassword",new_password)
                            .addFormDataPart("username",user_id)
                            .addFormDataPart("session",session)
                            .build();
                    url = "http://132.232.78.106:8001/api/changePwdByself/";
                    String response = "";
                    response = okhttp_plus.sendRequestwithOkHttp(url,requestBody,user_id,ChangePassword.this);
                    if (response != null){
                        showResponse(response);
                    }else {
                        Toast.makeText(ChangePassword.this,"请求失败，请重试",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(ChangePassword.this,"两次输入的新密码不一致",Toast.LENGTH_SHORT).show();
                }

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


    private void showResponse(final String response) {

        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String statu = jsonObject.getString("statu");
            final String msg = jsonObject.getString("msg");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChangePassword.this,msg,Toast.LENGTH_SHORT).show();
                }

            });
            if (statu.equals("1")){
                Intent intent = new Intent(ChangePassword.this,Login_Activity.class);
                finish();
                startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }
}
