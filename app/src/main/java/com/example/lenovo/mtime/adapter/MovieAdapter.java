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

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.Movie_Details_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie>mMovieList;
    private Context context;

    public MovieAdapter(Context context,List<Movie>movieList){
        this.context = context;
        mMovieList = movieList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView movieImage;
        TextView movieName;
        TextView movieInfo;
        TextView movieMark;
        TextView movieTime;

        public ViewHolder(View view){
            super(view);
            movieImage = view.findViewById(R.id.Movie_Image);
            movieInfo = view.findViewById(R.id.Movie_Info);
            movieName = view.findViewById(R.id.Movie_Name);
            movieMark = view.findViewById(R.id.Movie_Mark);
            movieTime = view.findViewById(R.id.Movie_Time);
        }
    }
    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        //点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //写到item点击事件，可以参考知乎日报
                int position = holder.getAdapterPosition();
                String Movie_id = (String) mMovieList.get(position).getMovieId();
                String Url = "106.13.106.1/film/i/film/" + Movie_id;

                Intent intent = new Intent(context, Movie_Details_Activity.class);
                intent.putExtra("Url",Url);
                context.startActivity(intent);
            }
        });

        return holder;  //写到adapter，明天继续

    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder viewHolder, int position) {
          Movie movie = mMovieList.get(position);

          viewHolder.movieTime.setText(movie.getTime());
          viewHolder.movieInfo.setText(movie.getMovieInfo());
//          viewHolder.movieImage.setImageResource(movie.getImage());

        Glide.with(context).load("http://39.96.208.176"+movie.getImage()).placeholder(R.drawable.eg).error(R.drawable.email_128).into(viewHolder.movieImage);
        viewHolder.movieMark.setText(movie.getMark());
        viewHolder.movieName.setText(movie.getMovieName());

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }
}
