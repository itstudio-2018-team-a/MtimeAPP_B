package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.NewsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private List<News> list;
    private Context context;
    public String user_id;
    public String session;
    private boolean hasMore = true;


    public NewsAdapter(List<News> list,String userName1,Context context,String session){
        user_id = userName1;
        this.list = list;
        this.context = context;
        this.session = session;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_news;
        TextView tv_newsTitle;
        View newsView;
        TextView tv_newsAuthor;

        public ViewHolder(View view) {
            super(view);
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
                String newsId = String.valueOf(news.getId());

                //将判断条件由session改为了flag
                if(Login_Activity.flag == null||Login_Activity.flag.equals("0")) Toast.makeText(view.getContext(),"您还未登录，请先登录",Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent();
                    intent.setClass(view .getContext(), NewsDetail.class );
                    intent.putExtra("newsId", newsId);
                    Log.d("传过去的newsid",newsId);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("session", session);
                    view.getContext().startActivity(intent);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder, int i) {
        News news = list.get(i);
        viewHolder.tv_newsTitle.setText(news.getTitle());
        viewHolder.tv_newsAuthor.setText(news.getAuthor());
        Glide.with(context).load("http://132.232.78.106:8001/media/"+news.getPhoto()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_news);
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
    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<News> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            list.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }
}
