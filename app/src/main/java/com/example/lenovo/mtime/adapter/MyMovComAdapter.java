package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.MyMovCom;
import com.example.lenovo.mtime.bean.MyNewsCom;

import java.util.List;

public class MyMovComAdapter extends RecyclerView.Adapter<MyMovComAdapter.ViewHolder>{
    private List<MyMovCom> list;
    private Context context;
    public String user_id;
    public String session;


    public MyMovComAdapter(List<MyMovCom> list,String userName1,Context context,String session){
        user_id = userName1;
        this.list = list;
        this.context = context;
        this.session = session;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        View myMovComView;

        public ViewHolder(View view) {
            super(view);
            myMovComView = view;
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    public MyMovComAdapter(List<MyMovCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyMovComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user_newscom, viewGroup, false);
        final MyMovComAdapter.ViewHolder holder = new MyMovComAdapter.ViewHolder(view);
        holder.myMovComView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                MyMovCom myMovCom = list.get(position);


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyMovComAdapter.ViewHolder viewHolder, int i) {
        MyMovCom myMovCom = list.get(i);
        viewHolder.tv_title.setText("评："+myMovCom.getFirmName());
        viewHolder.tv_time.setText(myMovCom.getTime());
        viewHolder.tv_content.setText(myMovCom.getContent());
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
