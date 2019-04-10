package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.MakeLongCom;
import com.example.lenovo.mtime.Movie_Details_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.Movie;
import com.example.lenovo.mtime.bean.MovieCom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MovieComAdapter extends RecyclerView.Adapter<MovieComAdapter.ViewHolder> {
    private List<MovieCom> list;
    private Context context;
    private String userName;
    private String session;
    private String movie_id;

    public MovieComAdapter(List<MovieCom> list, String userName1, Context context,String session,String movie_id) {
        userName = userName1;
        this.list = list;
        this.context = context;
        this.session = session;
        this.movie_id = movie_id;
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
                String authorName = movieCom.getAuthor();
                    Snackbar.make(view,"确定要删除这条影论吗",Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendRequestWithOkHttp(movieCom,view);
                                }
                            })
                            .show();
                return true;
            }
        });
        return holder;
    }

    private void sendRequestWithOkHttp(final MovieCom movieCom, final View view){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(20,TimeUnit.SECONDS)
                            .build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("session",session)
                            .add("id",String.valueOf(movieCom.getId()))
                            .add("type","0")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/deleteMyNewsComment/")
                            .post(requestBody)
                            .addHeader("Connection","close")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("删除影评的返回数据",responseDate);
                    JSONTokener(responseDate);
                    JSONObject jsonObject = new JSONObject(responseDate);
                    final int state = jsonObject.getInt("state");
                    String msg = jsonObject.getString("msg");
                    Looper.prepare();
                    if (state == 1){
                        Toast.makeText(view.getContext(),"删除成功",Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(view.getContext(), Movie_Details_Activity.class);
//                        intent.putExtra("user_id",userName);
//                        intent.putExtra("session",session);
//                        intent.putExtra("movie_id",movie_id);
//                        view.getContext().startActivity(intent);
                    }else if (state == -1) {
                        Toast.makeText(view.getContext(), "您还没有登录，请先登录", Toast.LENGTH_LONG).show();
                    }else if (state == -2) {
                        Toast.makeText(view.getContext(), "删除失败", Toast.LENGTH_LONG).show();
                    }
                    Looper.loop();

                }catch (Exception e){

                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException){
                                Toast.makeText(view.getContext(),"连接超时",Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException){
                                Toast.makeText(view.getContext(),"连接异常",Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(view.getContext(),"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                            }


                }
            }
        }).start();
    }

    private static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }


    @Override
    public void onBindViewHolder(@NonNull MovieComAdapter.ViewHolder viewHolder, int i) {
        MovieCom movieCom = list.get(i);
        Glide.with(context).load("http://132.232.78.106:8001/media/" + movieCom.getAutherHeadPhoto()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_author.setText(movieCom.getAuthor());
        viewHolder.tv_context.setText(movieCom.getContent());
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
