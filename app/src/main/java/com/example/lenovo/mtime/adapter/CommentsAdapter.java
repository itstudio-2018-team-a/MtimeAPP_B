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
import com.example.lenovo.mtime.bean.Comments;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comments> list;
    private Context context;
    public String user_id;

    public CommentsAdapter(List<Comments> list, String user_id, Context context){
        this.user_id = user_id;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv_movie;
        TextView tv_commentsTitle;
        TextView tv_summary;
        TextView tv_movieTitle;
        ImageView iv_author;
        TextView tv_commentsAuthor;
        View commentsView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            commentsView = view;
            iv_movie = (ImageView) view.findViewById(R.id.iv_movie);
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_commentsAuthor = (TextView) view.findViewById(R.id.tv_commentsAuthor);
            tv_commentsTitle = (TextView) view.findViewById(R.id.tv_commentsTitle);
            tv_summary = (TextView) view.findViewById(R.id.tv_summary);
            tv_movieTitle = (TextView) view.findViewById(R.id.tv_movieTitle);
        }
    }

    public CommentsAdapter(List<Comments> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comments, viewGroup, false);
        final CommentsAdapter.ViewHolder holder = new CommentsAdapter.ViewHolder(view);
        holder.commentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Comments comments = list.get(position);
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
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder viewHolder, int i) {
        Comments comments = list.get(i);
        viewHolder.tv_movieTitle.setText(comments.getMovieTitle());

        viewHolder.iv_movie.setImageBitmap(comments.getMovieImage());
        viewHolder.iv_author.setImageBitmap(comments.getAuthorImage());
        viewHolder.tv_commentsAuthor.setText(comments.getAuthor());
        viewHolder.tv_commentsTitle.setText(comments.getTitle());
        viewHolder.tv_summary.setText(comments.getSummary());
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
