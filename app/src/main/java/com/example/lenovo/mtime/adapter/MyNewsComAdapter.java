package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.NewsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.MyNewsCom;
import com.example.lenovo.mtime.bean.News;

import java.util.List;

public class MyNewsComAdapter extends RecyclerView.Adapter<MyNewsComAdapter.ViewHolder>{
    private List<MyNewsCom> list;
    private Context context;
    public String user_id;
    public String session;


    public MyNewsComAdapter(List<MyNewsCom> list,String userName1,Context context,String session){
        user_id = userName1;
        this.list = list;
        this.context = context;
        this.session = session;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        View myNewsComView;

        public ViewHolder(View view) {
            super(view);
            myNewsComView = view;
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    public MyNewsComAdapter(List<MyNewsCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyNewsComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user_newscom, viewGroup, false);
        final MyNewsComAdapter.ViewHolder holder = new MyNewsComAdapter.ViewHolder(view);
        holder.myNewsComView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                MyNewsCom myNewsCom = list.get(position);


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewsComAdapter.ViewHolder viewHolder, int i) {
        MyNewsCom myNewsCom = list.get(i);
        viewHolder.tv_title.setText("评："+myNewsCom.getNewsTitle());
        viewHolder.tv_time.setText(myNewsCom.getCreate_time());
        viewHolder.tv_content.setText(myNewsCom.getContent());
    }

    @Override
    public int getItemCount() {
        if(null==list) return 0;
        else return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}
