package com.example.lenovo.mtime.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class MovieComingFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private List<Movie> movies=new ArrayList<>();
    private MovieAdapter movieAdapter;
    private String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.moviecomingfragment,container,false);
        movies.clear();

        recyclerView = view.findViewById(R.id.Recycleview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) user_id = bundle.getString("user_id");
        //测试用
//        for (int i = 0; i <= 10 ; i++){
//            Movie movie = new Movie();
//            movie.setMark("9.8");
//            movie.setMovieId(i+"");
//            movie.setMovieName("复仇者联盟4：终局之战");
//            movie.setTime("2019.06.07");
//            movies.add(movie);
//        }

//        LinearLayoutManager manager=new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(manager);
//        movieAdapter = new MovieAdapter(getContext(), movies);
//        recyclerView.setAdapter(movieAdapter);
        sendRequestWithOkHttp();
    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
//                    OkHttpClient client = new OkHttpClient();
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getFilmList/")
                            .addHeader("head","1")
                            .addHeader("type","0")
                            .addHeader("number","12")
                            .build();

                    Response response = client.newCall(request).execute();
//                    String cookie = response.header("Set-Cookie");  //获取cookie

                    //将cookie储存到sharedpreference
//                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",MODE_PRIVATE).edit();
//                    editor.putString("cookie",cookie);
//                    editor.apply();

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
            int state = jsonObject.getInt("state");
            String list = jsonObject.getString("result");
//            String status = jsonObject.getString("status");

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
                movieAdapter = new MovieAdapter(getContext(), movies,user_id);
                recyclerView.setAdapter(movieAdapter);
            }
        });
    }
}
