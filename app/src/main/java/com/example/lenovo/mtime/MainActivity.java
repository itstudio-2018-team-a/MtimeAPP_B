package com.example.lenovo.mtime;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
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


        if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
            //弹出一个选择框
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("您需要登录吗？");
            builder.setNegativeButton("朕不需要", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("废话，赶紧", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this,Login_Activity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(true);      //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
            dialog.show();
        }
            //从登陆界面获取userid
            Intent intent = getIntent();
            user_id = intent.getStringExtra("user_id");
            String session = intent.getStringExtra("session");
            String nickName = intent.getStringExtra("nickName");
            String headImage = intent.getStringExtra("headImage");
            String email = intent.getStringExtra("email");

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
