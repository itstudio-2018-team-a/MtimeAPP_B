package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
            tv_commentator = (TextView) view.findViewById(R.id.tv_commentator);
            tv_context = (TextView) view.findViewById(R.id.tv_context);
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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_comments, viewGroup, false);
        final NewsComAdapter.ViewHolder holder = new NewsComAdapter.ViewHolder(view);
        holder.newsComView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                NewsCom newsCom = list.get(position);
                // String newsId = news.getNewsId();
                //Intent intent = new Intent();
                //intent.setClass(view .getContext(),ShowNews.class );
                //intent.putExtra("newsId", newsId);
                // intent.putExtra("userName", userName);
                //  view.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsComAdapter.ViewHolder viewHolder, int i) {
        NewsCom newsCom = list.get(i);
        viewHolder.iv_author.setImageBitmap(newsCom.getAuthorImage());
        viewHolder.tv_commentator.setText(newsCom.getAuthor());
        viewHolder.tv_time.setText(newsCom.getTime());
        viewHolder.tv_context.setText(newsCom.getContext());
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
