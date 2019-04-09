package com.example.lenovo.mtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class FindPasswordActivity extends AppCompatActivity {
    EditText User_id;
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
        newPassword = findViewById(R.id.et_newPassword);
        repeatPassword = findViewById(R.id.et_repeatPassword);
        Code = findViewById(R.id.et_code);
        btn_sendCode = findViewById(R.id.send_code);
        btn_Reset = findViewById(R.id.btn_reset);
        btn_Cancel = findViewById(R.id.btn_out);
    }
}
