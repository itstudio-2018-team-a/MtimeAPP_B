package com.example.lenovo.mtime.fragment;

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

        btn_login.setOnClickListener(new View.OnClickListener() {
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
