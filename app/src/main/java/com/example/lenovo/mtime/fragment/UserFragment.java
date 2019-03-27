package com.example.lenovo.mtime.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.R;

import de.hdodenhof.circleimageview.CircleImageView;

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


        if(Login_Activity.flag == null){
            tv_userName.setText("欢迎来到时光网");

        }else {
            Intent intent = getActivity().getIntent();
            String User_id = intent.getStringExtra("extra_data");  //根据登陆界面传过来的账号获取用户信息

            //userName = 这里写从服务器获取到的用户名
            tv_userName.setText(userName);  //写到个人中心跳转，明天细化登录界面并测试post
            //显示用户名

        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_Activity.flag == null){
                    Intent intent = new Intent(getContext(), Login_Activity.class);
                    startActivity(intent);

                }else {
                    //这里展示头像

                }

            }
        });
        btn_movieComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(UserFragment.this,.class);
                //intent.putExtra("userName",userName);
                //startActivity(intent);
            }
        });
        btn_newsComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(UserFragment.this,.class);
                //intent.putExtra("userName",userName);
                //startActivity(intent);
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
//                Intent intent = new Intent(getContext(),Login_Activity.class);
//                intent.putExtra("userName",userName);
//                startActivity(intent);
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
                //Intent intent = new Intent(UserFragment.this,.class);
                //intent.putExtra("userName",userName);
                //startActivity(intent);
            }
        });
        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(UserFragment.this,.class);
                //intent.putExtra("userName",userName);
                //startActivity(intent);
            }
        });

        return view;
    }

}
