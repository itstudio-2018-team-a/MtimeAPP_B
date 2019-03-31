package com.example.lenovo.mtime.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.ChangeName;
import com.example.lenovo.mtime.ChangePassword;
import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.Show_HeadImage;
import com.example.lenovo.mtime.User_comments;
import com.example.lenovo.mtime.adapter.MovieAdapter;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {
    View view;
    CircleImageView user_image;
    RelativeLayout btn_login;
    Button btn_newsComments;
    Button btn_movieComments;
    LinearLayout btn_findPassword;
    LinearLayout btn_logout;
    LinearLayout btn_changePassword;
    LinearLayout btn_changeName;
    TextView tv_userName;
    String userName;
    private String user_id;
    private String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user,container,false);

        //绑监听
        user_image = (CircleImageView) view.findViewById(R.id.user_image);
        btn_login = (RelativeLayout) view.findViewById(R.id.btn_login);
        btn_movieComments = (Button) view.findViewById(R.id.btn_movieComments);
        btn_newsComments = (Button) view.findViewById(R.id.btn_newsComments);
        btn_logout = (LinearLayout) view.findViewById(R.id.btn_logout);
        btn_findPassword = (LinearLayout) view.findViewById(R.id.btn_findPassword);
        btn_changeName = (LinearLayout) view.findViewById(R.id.btn_changeName);
        btn_changePassword = (LinearLayout) view.findViewById(R.id.btn_changePassword);
        tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Login_Activity.flag == null){
            tv_userName.setText("欢迎来到时光网");

        }else {
            Intent intent = getActivity().getIntent();
            //根据登陆界面传过来的账号获取用户信息
            user_id = intent.getStringExtra("extra_data");
            url = "106.13.106.1/account/i/user/info/" + user_id;
            sendRequestWithOkHttp();  //发起网络请求从服务器获取相关用户数据

        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_Activity.flag == null){
                    Intent intent = new Intent(getContext(), Login_Activity.class);
                    startActivity(intent);

                }else {
                    //这里展示高清头像
                    Intent intent = new Intent(getContext(), Show_HeadImage.class);
                    startActivity(intent);

                }

            }
        });
        btn_movieComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型","电影评论");
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
        btn_newsComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型","新闻评论");
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    //弹出一个确认框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("您确定要退出登录吗？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "已取消", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), Login_Activity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                }
            }
        });

        btn_findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(UserFragment.this,.class);
                //intent.putExtra("userName",userName);
                //startActivity(intent);
            }
        });

        btn_changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {

                Intent intent = new Intent(getContext(), ChangeName.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
            }
        });
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(getContext(), ChangePassword.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }
            }
        });

    }

    private void sendRequestWithOkHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)   //网址有待改动
                            .build();

                    Response response = client.newCall(request).execute();
                    String cookie = response.header("Set-Cookie");  //获取cookie

                    //将cookie储存到sharedpreference
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("cookie",cookie);
                    editor.apply();

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
        final User user = gson.fromJson(response,User.class);

        getActivity().runOnUiThread(new Runnable(){           //fragment中好像不能直接使用该方法，故加了getactivity（）；
            @Override
            public void run(){
                //设置ui

                Glide.with(getContext()) //显示头像
                        .load(user.getHeadImage_url())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(user_image);

                tv_userName.setText(user.getUser_Name());
            }
        });
    }
}
