package com.example.lenovo.mtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterInfor_Activity extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_account;
    private EditText ed_password;
    private EditText ed_email;
    private EditText ed_code;
    private EditText ed_password_sure;
    private Button btn_register;
    private Button btn_get_code;
    ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_infor);

        ed_account = findViewById(R.id.ed_account);
        ed_password = findViewById(R.id.ed_password);
        ed_email = findViewById(R.id.ed_email);
        ed_code = findViewById(R.id.ed_code);
        ed_password_sure = findViewById(R.id.ed_password_sure);
        btn_register = findViewById(R.id.btn_register);
        btn_get_code = findViewById(R.id.btn_get_code);

        String account = ed_account.getText().toString();
        String password = ed_password.getText().toString();
        String password_sure = ed_password_sure.getText().toString();
        String code = ed_code.getText().toString();
        String email = ed_email.getText().toString();



        btn_register.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:

                break;

            case R.id.btn_get_code:

                break;
                default:
                    break;

        }
    }
}
