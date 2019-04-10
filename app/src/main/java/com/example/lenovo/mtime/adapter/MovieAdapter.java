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
import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.Movie_Details_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie>mMovieList;
    private Context context;
    private String user_id;
    private final String session;

    public MovieAdapter(Context context,List<Movie>movieList,String user_id,String session){
        this.context = context;
        mMovieList = movieList;
        this.user_id = user_id;
        this.session = session;
        Log.d("hhh","打印MovieAdapter中的user_id="+user_id);
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
                if (Login_Activity.flag==null||Login_Activity.flag.equals("0"))
                    Toast.makeText(context,"请先登录",Toast.LENGTH_SHORT).show();
                else {
                int position = holder.getAdapterPosition();
                Movie movie = mMovieList.get(position);
                String movie_id = String.valueOf(movie.getFilm_id());

                Intent intent = new Intent(context, Movie_Details_Activity.class);
                intent.putExtra("user_id",user_id);
                intent.putExtra("session",session);
                intent.putExtra("movie_id",movie_id);
                context.startActivity(intent);
                }
            }
        });

        return holder;  //写到adapter，明天继续

    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder viewHolder, int position) {
          Movie movie = mMovieList.get(position);

          viewHolder.movieTime.setText(movie.getRelease_date()+" 上映");
          viewHolder.movieInfo.setText("“"+movie.getInfo()+"”");
//          viewHolder.movieImage.setImageResource(movie.getImage());
        Glide.with(context).load("http://132.232.78.106:8001"+movie.getImage()).placeholder(R.drawable.eg).error(R.drawable.email_128).into(viewHolder.movieImage);
        viewHolder.movieMark.setText(String.valueOf(movie.getMark())+"分");
        viewHolder.movieName.setText(movie.getTitle());

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }
}
