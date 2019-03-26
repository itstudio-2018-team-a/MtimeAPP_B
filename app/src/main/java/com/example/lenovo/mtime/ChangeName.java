package com.example.lenovo.mtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lenovo.mtime.fragment.UserFragment;

public class ChangeName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        EditText et_newName;
        Button btn_reset = (Button) findViewById(R.id.btn_reset);
        Button btn_out = (Button) findViewById(R.id.btn_out);
        final String userName;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeName.this, UserFragment.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
