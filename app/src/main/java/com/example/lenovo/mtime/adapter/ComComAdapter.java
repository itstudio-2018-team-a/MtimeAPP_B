package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.ComCom;
import com.example.lenovo.mtime.bean.NewsCom;

import java.util.List;

public class ComComAdapter extends RecyclerView.Adapter<ComComAdapter.ViewHolder> {
    private List<ComCom> list;
    private Context context;
    public String userName;

    public ComComAdapter(List<ComCom> list, String userName1, Context context){
        userName = userName1;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_commentator;
        TextView tv_context;
        TextView tv_time;
        View comComView;

        public ViewHolder(View view) {
            super(view);
            comComView = view;
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_commentator = (TextView) view.findViewById(R.id.tv_commentator);
            tv_context = (TextView) view.findViewById(R.id.tv_context);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    public ComComAdapter(List<ComCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ComComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_comments, viewGroup, false);
        final ComComAdapter.ViewHolder holder = new ComComAdapter.ViewHolder(view);
        holder.comComView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                ComCom comCom = list.get(position);
                View comComView = view;
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComComAdapter.ViewHolder viewHolder, int i) {
        ComCom comCom = list.get(i);
        Glide.with(context).load("http://39.96.208.176"+comCom.getAuthor_head()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_commentator.setText(comCom.getAuthor_name());
        viewHolder.tv_context.setText(comCom.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
}
