package com.example.lenovo.mtime.adapter;

import android.content.Context;
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
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.MovieCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MovieComAdapter extends RecyclerView.Adapter<MovieComAdapter.ViewHolder> {
    private List<MovieCom> list;
    private Context context;
    private String userName;

    public MovieComAdapter(List<MovieCom> list, String userName1, Context context) {
        userName = userName1;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_author;
        TextView tv_context;
        View movieComView;

        public ViewHolder(View view) {
            super(view);
            movieComView = view;
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_author = (TextView) view.findViewById(R.id.tv_author);
            tv_context = (TextView) view.findViewById(R.id.tv_content);
        }
    }

    public MovieComAdapter(List<MovieCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MovieComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if (context == null) {
            context = viewGroup.getContext();
        }
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.item_news_comments, viewGroup, false);
        final MovieComAdapter.ViewHolder holder = new MovieComAdapter.ViewHolder(view);
        holder.movieComView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                final MovieCom movieCom = list.get(position);
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieComAdapter.ViewHolder viewHolder, int i) {
        MovieCom movieCom = list.get(i);
        Glide.with(context).load("http://132.232.78.106:8001/media/" + movieCom.getAutherHeadPhoto()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_author.setText(movieCom.getAuthor());
        viewHolder.tv_context.setText(movieCom.getTitle());
    }

    @Override
    public int getItemCount() {
        if (null == list) return 0;
        else return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
