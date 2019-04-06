package com.example.lenovo.mtime.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.adapter.CommentsAdapter;
import com.example.lenovo.mtime.bean.Comments;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentsFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    private List<Comments> commentsList;
    private CommentsAdapter commentsAdapter;
    String user_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comments,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sendRequestWithOkHttp();

    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/getHotFilmReview/")   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.e("response",response.toString());
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
            Log.e("response",response);
            String list = jsonObject.getString("result");

            commentsList = gson.fromJson(list, new TypeToken<List<Comments>>(){}.getType());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                //设置ui
                LinearLayoutManager manager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);

                commentsAdapter = new CommentsAdapter(commentsList,user_id,getContext());

                recyclerView.setAdapter(commentsAdapter);
            }
        });
    }
}
