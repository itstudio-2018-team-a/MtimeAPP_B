package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsDetail extends AppCompatActivity {

    String newsId;
    String user_id;
    FloatingActionButton fab;
    TextView tv_author;
    TextView tv_newsTitle;
    TextView tv_pubTime;
    ImageView iv_photo;
    TextView tv_content;
    TextView tv_hint;

    int id;
    String author;
    String photo;
    String Time;
    String Title;
    int replyNum;
    String content;
    boolean isGood;
    Bitmap bitmap;
    String replys;
    String session;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Intent intent = getIntent();
        newsId = intent.getStringExtra("newsId");
        user_id = intent.getStringExtra("user_id");
//        session = intent.getStringExtra("session");
//        Log.d("评论时的新闻id",newsId);
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        session = sharedPreferences.getString("session","");

        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_newsTitle = (TextView) findViewById(R.id.tv_newsTitle);
        tv_pubTime = (TextView) findViewById(R.id.tv_pubTime);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        //tv_hint = findViewById(R.id.tv_hint);

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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id == null)
                {
                    Toast.makeText(NewsDetail.this, "您还没有登录，不能发表评论", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent1 = new Intent(NewsDetail.this,MakeNewsCom .class);
                    intent1.putExtra("user_id",user_id);
                    intent1.putExtra("newsId",newsId);
                    intent1.putExtra("session",session);

                    startActivity(intent1);
                }
            }
        });

        struct();

        //sendRequestWithOkHttp();

    }

    @Override
    protected void onStart() {
        super.onStart();
        sendRequestWithOkHttp();
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
                TextView tv_comment = (TextView)findViewById(R.id.tv_comment);
                if (Login_Activity.flag==null||Login_Activity.flag.equals("0"))
                    Toast.makeText(NewsDetail.this,"您还未登录，请先登录",Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(NewsDetail.this, NewsComActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("newsId", newsId);

                    intent.putExtra("replys", replys);
                    intent.putExtra("session", session);
                    intent.putExtra("replyNum",replyNum);

                    startActivity(intent);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();
                    FormBody.Builder builder = new FormBody.Builder();
                    Log.d("联网时的新闻id",newsId);
                    builder.add("id", newsId);
                    builder.add("operaType", "1");
                    builder.add("session", session);
                    RequestBody requestBody = builder.build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointNews/")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);

                    JSONObject jsonObject = new JSONObject(responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");

                    parseJSONWithGSON(result);

                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(NewsDetail.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(NewsDetail.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(NewsDetail.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    public static Bitmap getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void parseJSONWithGSON(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            id = jsonObject.getInt("id");
            author = jsonObject.getString("author");
            photo = jsonObject.getString("photo");
            Time = jsonObject.getString("Time");
            Title = jsonObject.getString("Title");
            replyNum = jsonObject.getInt("replyNum");
            content = jsonObject.getString("content");
            isGood = jsonObject.getBoolean("isGood");
            replys = jsonObject.getJSONArray("replys").toString();
            bitmap = getHttpBitmap("http://132.232.78.106:8001/media/"+photo);
            Log.d("hhh",content);
            showResponse();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_author.setText(author);
                tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
                tv_content.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页
                tv_content.setText(Html.fromHtml(content, imgGetter, null));
                tv_newsTitle.setText(Title);
                tv_pubTime.setText(Time);
                iv_photo.setImageBitmap(bitmap);
                TextView tv_comment = (TextView)findViewById(R.id.tv_comment);
                if(!String.valueOf(replyNum).equals("")) tv_comment.setText(String.valueOf(replyNum));
                //if (replyNum == 0) tv_hint.setText("当前新闻没有评论");
            }
        });
    }

    Html.ImageGetter imgGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Log.i("RG", "source---?>>>" + source);
            Drawable drawable = null;
            URL url;
            try {
                url = new URL(source);
                Log.i("RG", "url---?>>>" + url);
                drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            Log.i("RG", "url---?>>>" + url);
            return drawable;
        }
    };

    public static void struct() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork() // or
                // .detectAll()
                // for
                // all
                // detectable
                // problems
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .penaltyLog() // 打印logcat
                .penaltyDeath().build());
    }
}
