package com.example.lenovo.mtime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

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

public class CommentsDetail extends AppCompatActivity {

    private Context context;

    String commentsId;
    String session;
    String user_id;

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
    String poster;
    String userImg;

    TextView tv_title;
    TextView tv_content;
    TextView tv_author;
    ImageView iv_author;
    ImageView iv_movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_detail);

        context = this;

        Intent intent = getIntent();
        commentsId = intent.getStringExtra("commentsId");
        session = intent.getStringExtra("session");
        user_id = intent.getStringExtra("user_id");
        userImg = intent.getStringExtra("userImg");
        poster = intent.getStringExtra("poster");

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

        tv_author = (TextView) findViewById(R.id.tv_author);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_title=(TextView) findViewById(R.id.tv_title);
        iv_author = (ImageView) findViewById(R.id.iv_author);
        iv_movie = (ImageView) findViewById(R.id.iv_movie);

        struct();

        sendRequestWithOkHttp();
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
                    RequestBody requestBody = new FormBody.Builder()
                            .add("id",commentsId)
                            .add("session",session)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getPointFilmReview/")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseDate);
                    Log.d("hahaha",responseDate);
                    int state = jsonObject.getInt("state");
                    String result = jsonObject.getString("result");

                    parseJSONWithGSON(result);

                    JSONTokener(responseDate);
                    Log.d("hahaha",responseDate);

                }catch (final Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(CommentsDetail.this,"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(CommentsDetail.this,"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(CommentsDetail.this,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
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
            //photo = jsonObject.getString("photo");
            Time = jsonObject.getString("Time");
            Title = jsonObject.getString("Title");
            replyNum = jsonObject.getInt("replyNum");
            content = jsonObject.getString("content");
            isGood = jsonObject.getBoolean("isGood");
            replys = jsonObject.getJSONArray("replys").toString();
            //bitmap = getHttpBitmap("http://132.232.78.106:8001/media/"+photo);
            Log.d("hhh",content);
            showResponse();
            //newsList = gson.fromJson(list, new TypeToken<List<News>>(){}.getType());

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
                tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
                tv_content.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页
                tv_content.setText(Html.fromHtml(content, imgGetter, null));
                tv_author.setText(author+"（作者）");
                tv_title.setText(Title);

                Glide.with(context).load("http://132.232.78.106:8001"+poster).placeholder(R.drawable.eg).error(R.drawable.code_128).into(iv_movie);
                Glide.with(context).load("http://132.232.78.106:8001"+userImg).placeholder(R.drawable.eg).error(R.drawable.code_128).into(iv_author);
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
