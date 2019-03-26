package com.example.lenovo.mtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MakeNewsCom extends AppCompatActivity {
    EditText et_comments;
    Button btn_publish;
    String comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_news_com);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        et_comments = (EditText) findViewById(R.id.et_comments);
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = et_comments.getText().toString();
            }
        });
    }
}
