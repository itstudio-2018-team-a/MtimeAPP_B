package com.example.lenovo.mtime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MarkActivity extends AppCompatActivity {

    Button btn_shortComment;
    Button btn_longComment;

    String user_id;
    String movie_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);

        btn_longComment = (Button) findViewById(R.id.btn_longComment);
        btn_shortComment = (Button) findViewById(R.id.btn_shortComment);

        final Intent intent = new Intent();
        user_id = intent.getStringExtra("user_id");
        movie_id = intent.getStringExtra("movie_id");

        btn_shortComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MarkActivity.this,MakeShortCom.class);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("movie_id",movie_id);
                startActivity(intent1);
            }
        });

        btn_longComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MarkActivity.this,MakeShortCom.class);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("movie_id",movie_id);
                startActivity(intent1);
            }
        });

    }
}
