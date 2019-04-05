package com.example.lenovo.mtime.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class MovieFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private List<Movie> movies=new ArrayList<>();
    private MovieAdapter movieAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<String> TitleList = new ArrayList<>();  //页卡标题集合
    private ArrayList<Fragment> ViewList = new ArrayList<>();   //页卡视图集合
    private Fragment movieComingFragment,movieShowingFragment;  //页卡视图
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_movie,container,false);
        TitleList.clear();//加载布局前首先清空list，避免出现左右滑动时布局重新加载出现内容重复
        ViewList.clear();
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        //recyclerView = view.findViewById(R.id.Recycleview);
        return view;
}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
       super.onActivityCreated(savedInstanceState);


        Bundle bundle = getArguments();
        if(bundle != null) user_id = bundle.getString("user_id");

        movieShowingFragment = new MovieShowingFragment();
        movieComingFragment = new MovieComingFragment();

        //添加页卡视图
        ViewList.add(movieShowingFragment);
        ViewList.add(movieComingFragment);

        //添加页卡标题
        TitleList.add("正在热映");
        TitleList.add("即将上映");

        //设置tab模式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(TitleList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(TitleList.get(1)));

        //设置adapter
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()){
            //fragment嵌套fragment时需要用到getchildfragmentmanager方法

            //获取每个页卡
            @Override
            public android.support.v4.app.Fragment getItem(int position){
                return ViewList.get(position);
            }

            //获取页卡数
            @Override
            public int getCount(){
                return  TitleList.size();
            }

            //获取页卡标题
            @Override
            public CharSequence getPageTitle(int position){
                return TitleList.get(position);
            }
        });

        //tab与viewpager绑定
        tabLayout.setupWithViewPager(viewPager);


      // sendRequestWithOkHttp();

    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://www/film/i/film_lsit")   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String cookie = response.header("Set-Cookie");  //获取cookie

                    //将cookie储存到sharedpreference
                   SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",MODE_PRIVATE).edit();
                   editor.putString("cookie",cookie);
                   editor.apply();

                   // Cookie cookie = client.cookieJar().saveFromResponse("http://www/film/i/film_lsit",cookies);
                    String responseDate = response.body().string();
                    showResponse(responseDate);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response){

        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int num = jsonObject.getInt("num");
            String list = jsonObject.getString("list");
            String statues = jsonObject.getString("statues");

            movies = gson.fromJson(list, new TypeToken<List<Movie>>(){}.getType());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
                    public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);

                movieAdapter = new MovieAdapter(getContext(), movies, user_id);

                recyclerView.setAdapter(movieAdapter);
            }
        });
    }
}