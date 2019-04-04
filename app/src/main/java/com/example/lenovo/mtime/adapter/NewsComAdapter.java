package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.NewsComActivity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.News;
import com.example.lenovo.mtime.bean.NewsCom;

import java.util.List;

public class NewsComAdapter extends RecyclerView.Adapter<NewsComAdapter.ViewHolder>{
    private List<NewsCom> list;
    private Context context;
    public String userName;

    public NewsComAdapter(List<NewsCom> list, String userName1, Context context){
        userName = userName1;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_commentator;
        TextView tv_context;
        TextView tv_time;
        View newsComView;

        public ViewHolder(View view) {
            super(view);
            newsComView = view;
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_commentator = (TextView) view.findViewById(R.id.tv_author);
            tv_context = (TextView) view.findViewById(R.id.tv_content);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }

    public NewsComAdapter(List<NewsCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NewsComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_comments, viewGroup, false);
        final NewsComAdapter.ViewHolder holder = new NewsComAdapter.ViewHolder(view);
        holder.newsComView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                NewsCom newsCom = list.get(position);
                View newsComView = view;
                String authorName = newsCom.getAuthor_name();
                if(userName.equals(authorName))
                {
                    Snackbar.make(newsComView,"确定要删除这条评论吗",Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }
                else Toast.makeText(v .getContext(), "不是您的评论，不能删除", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsComAdapter.ViewHolder viewHolder, int i) {
        NewsCom newsCom = list.get(i);
        Glide.with(context).load("http://39.96.208.176"+newsCom.getAuthor_head()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_commentator.setText(newsCom.getAuthor_name());
        viewHolder.tv_time.setText(newsCom.getTime());
        viewHolder.tv_context.setText(newsCom.getContent());
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
