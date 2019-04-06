package com.example.lenovo.mtime;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lenovo.mtime.adapter.ViewPagerAdapter;
import com.example.lenovo.mtime.fragment.CommentsFragment;
import com.example.lenovo.mtime.fragment.MovieFragment;
import com.example.lenovo.mtime.fragment.NewsFragment;
import com.example.lenovo.mtime.fragment.UserFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //绑监听
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //TabLayout+Fragment+ViewPager
        final ArrayList<Fragment> fragments = new ArrayList<>();
        CommentsFragment commentsFragment = new CommentsFragment();
        MovieFragment movieFragment = new MovieFragment();
        NewsFragment newsFragment = new NewsFragment();
        UserFragment userFragment = new UserFragment();
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
}
