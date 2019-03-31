package com.example.lenovo.mtime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDetail extends AppCompatActivity {

    String newsId;
    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        newsId = intent.getStringExtra("newsId");
        user_id = intent.getStringExtra("user_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        final MenuItem item = menu.findItem(R.id.comment);
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
            case R.id.comment:
                Intent intent = new Intent(NewsDetail.this, NewsComActivity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("newsId",newsId);
                startActivity(intent);
                break;
            case R.id.fab:
                Intent intent1 = new Intent(NewsDetail.this,MakeNewsCom .class);
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("newsId",newsId);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return true;
    }
}
