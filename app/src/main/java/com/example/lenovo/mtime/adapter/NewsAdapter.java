package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private List<News> list;
    private Context context;
    public String userName;

    public NewsAdapter(List<News> list,String userName1,Context context){
        userName = userName1;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv_news;
        TextView tv_newsTitle;
        View newsView;
        TextView tv_newsAuthor;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsView = view;
            iv_news = (ImageView) view.findViewById(R.id.iv_news);
            tv_newsTitle = (TextView) view.findViewById(R.id.tv_newsTitle);
            tv_newsAuthor = (TextView) view.findViewById(R.id.tv_newsAuthor);
        }
    }

    public NewsAdapter(List<News> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.newsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                News news = list.get(position);
                String newsId = news.getNewsId();
                Intent intent = new Intent();
//                intent.setClass(view .getContext(),ShowNews.class );
                intent.putExtra("newsId", newsId);
                intent.putExtra("userName", userName);
                view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder, int i) {
        News news = list.get(i);
        viewHolder.tv_newsTitle.setText(news.getTitle());
        viewHolder.iv_news.setImageBitmap(news.getNewsImage());
        viewHolder.tv_newsAuthor.setText(news.getAuthor());
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
