package com.example.lenovo.mtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lenovo.mtime.adapter.ViewPagerAdapter;
import com.example.lenovo.mtime.fragment.CommentsFragment;
import com.example.lenovo.mtime.fragment.MovieFragment;
import com.example.lenovo.mtime.fragment.NewsFragment;
import com.example.lenovo.mtime.fragment.UserFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    String user_id;
    private UserFragment userFragment;
    private NewsFragment newsFragment;
    private MovieFragment movieFragment;
    private CommentsFragment commentsFragment;
    private String password;
    private String result;
    private String session;
    private String nickName;
    private String headImage;
    private String email;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //绑监听
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //TabLayout+Fragment+ViewPager
        final ArrayList<Fragment> fragments = new ArrayList<>();
        commentsFragment = new CommentsFragment();
        movieFragment = new MovieFragment();
        newsFragment = new NewsFragment();
        userFragment = new UserFragment();
        fragments.add(newsFragment);
        fragments.add(commentsFragment);
        fragments.add(movieFragment);
        fragments.add(userFragment);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments,new String[]{"新闻", "影评", "购票", "我的"});
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        changeIconImgBottomMargin(tabLayout,4);
        //底部导航栏的图片切换
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Drawable d = null;
            switch (i) {
                case 0:
                    d = getResources().getDrawable(R.drawable.selector_news);
                    break;
                case 1:
                    d = getResources().getDrawable(R.drawable.selector_comments);
                    break;
                case 2:
                    d = getResources().getDrawable(R.drawable.selector_movie);
                    break;
                case 3:
                    d = getResources().getDrawable(R.drawable.selector_user);
                    break;
            }
            tab.setIcon(d);
        }

//        sendRequestWithOkHttp();

        if (Login_Activity.flag != null&&!Login_Activity.flag.equals("1")) {
            //从登陆界面获取userid
            Intent intent = getIntent();
            user_id = intent.getStringExtra("user_id");
            String session = intent.getStringExtra("session");
            String nickName = intent.getStringExtra("nickName");
            String headImage = intent.getStringExtra("headImage");
            String email = intent.getStringExtra("email");
        }
        Bundle bundle = new Bundle();
        bundle.putString("user_id",user_id);
        bundle.putString("session",session);
        bundle.putString("nickName",nickName);
        bundle.putString("headImage",headImage);
        bundle.putString("email",email);
        //将user_id发送到各个fragment
        newsFragment.setArguments(bundle);
        movieFragment.setArguments(bundle);
        commentsFragment.setArguments(bundle);
        userFragment.setArguments(bundle);

    }


    private void sendRequestWithOkHttp() {
        //开启现线程发起网络请求

        //从sharedpreferences获取账号密码
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);

            user_id = preferences.getString("user_id", "");
            password = preferences.getString("password", "");
        if (user_id != null&&password!=null&&!user_id.equals("")&&!password.equals("")) {
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {

                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题，然并...
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();

                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("username", user_id)
                            .addFormDataPart("password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/login/")   //网址有待改动
                            .post(requestBody)
                            .addHeader("Connection", "close")
                            .build();

                    Call call = client.newCall(request);
                    String responseDate = "";
                    try {
                        Response response = call.execute();
                        responseDate = response.body().string();

                        Log.d("ZGH", responseDate);
//                    cookie = response.header("Set-Cookie");  //获取cookie
//                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//                    editor.putString("cookie",cookie);

                        JSONArray jsonArray = new JSONArray(responseDate);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        result = jsonObject.getString("statu");

                        if (result.equals("1")) { //如果登陆成功，则读取以下数据
                            session = jsonObject.getString("session");
                            nickName = jsonObject.getString("nickName");
                            user_id = jsonObject.getString("username");
                            headImage = jsonObject.getString("headImage");
                            email = jsonObject.getString("email");

                        } else {  //若登陆失败则toast失败原因
                            msg = jsonObject.getString("msg");
                        }
                        showResult();


                    } catch (final Exception e) {
                        Log.e("ZGHhh", responseDate);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                e.printStackTrace();
                                if (e instanceof SocketTimeoutException) {
                                    Toast.makeText(MainActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                                }
                                if (e instanceof ConnectException) {
                                    Toast.makeText(MainActivity.this, "连接异常", Toast.LENGTH_SHORT).show();
                                }

                                if (e instanceof ProtocolException) {
                                    Toast.makeText(MainActivity.this, "未知异常，请稍后再试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }).start();
        }
    }
        void showResult() {
            runOnUiThread(new Runnable() {

                private SharedPreferences.Editor editor;

                @Override
                public void run() {
                    if (result.equals("1")) {
                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                        editor.putString("session", session);
                        editor.apply();
                    Toast.makeText(MainActivity.this, "自动登录成功", Toast.LENGTH_SHORT).show();
                         Login_Activity.flag = "1";
                    } else {
                        Toast.makeText(MainActivity.this, msg+"自动登陆失败，请尝试手动登陆", Toast.LENGTH_LONG).show();
                    }
                }
            });

    }
    private void changeIconImgBottomMargin(ViewGroup parent, int px){
        for(int i = 0; i < parent.getChildCount(); i++){
            View child = parent.getChildAt(i);
            if(child instanceof ViewGroup){
                changeIconImgBottomMargin((ViewGroup) child, px);
            }
            else if(child instanceof ImageView){
                ViewGroup.MarginLayoutParams lp = ((ViewGroup.MarginLayoutParams) child.getLayoutParams());
                lp.bottomMargin = 0;
                child.requestLayout();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //从登陆界面获取userid

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        String session = intent.getStringExtra("session");
        String nickName = intent.getStringExtra("nickName");
        String headImage = intent.getStringExtra("headImage");
        String email = intent.getStringExtra("email");

        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bundle.putString("session", session);
        bundle.putString("nickName", nickName);
        bundle.putString("headImage", headImage);
        bundle.putString("email", email);
        //将user_id发送到各个fragment
        newsFragment.setArguments(bundle);
        movieFragment.setArguments(bundle);
        commentsFragment.setArguments(bundle);
        userFragment.setArguments(bundle);
    }
}
