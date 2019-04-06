package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.example.lenovo.mtime.CommentsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Comments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comments> list;
    private Context context;
    public String user_id;
    private String session;

    public CommentsAdapter(List<Comments> list, String user_id, Context context){
        this.user_id = user_id;
        this.list = list;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_movie;
        TextView tv_commentsTitle;
        TextView tv_summary;
        TextView tv_movieTitle;
        ImageView iv_author;
        TextView tv_commentsAuthor;
        View commentsView;

        public ViewHolder(View view) {
            super(view);

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
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comments, viewGroup, false);
        final CommentsAdapter.ViewHolder holder = new CommentsAdapter.ViewHolder(view);
        holder.commentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Comments comments = list.get(position);
                String commentId = String.valueOf(comments.getComment_id());
                Intent intent = new Intent();
                intent.setClass(view .getContext(), CommentsDetail.class );
                intent.putExtra("commentsId", commentId);
                intent.putExtra("user_id", user_id);
                view.getContext().startActivity(intent);
            }
        });
        holder.commentsView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                final Comments comments = list.get(position);
                String authorName = comments.getAuthor_name();
                if(user_id.equals(authorName))
                {
                    Snackbar.make(view,"确定要删除这条影论吗",Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendRequestWithOkHttp(comments,view);
                                }
                            })
                            .show();
                }
                else if (user_id.equals("")) Toast.makeText(v .getContext(), "您还没有登录，请先登录", Toast.LENGTH_SHORT).show();
                else Toast.makeText(v .getContext(), "不是您的评论，不能删除", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder viewHolder, int i) {
        Comments comments = list.get(i);
        //viewHolder.tv_movieTitle.setText(comments.get);
        Log.d("hhh",comments.getAuthor_head());
        Glide.with(context).load("http://132.232.78.106:8001"+comments.getImage()).placeholder(R.drawable.eg).error(R.drawable.code_128).into(viewHolder.iv_movie);
        //Glide.with(context).load("http://132.232.78.106:8001/api"+comments.getAuthor_head()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_commentsAuthor.setText(comments.getAuthor_name());
        viewHolder.tv_commentsTitle.setText(comments.getTitle());
        viewHolder.tv_summary.setText(comments.getSubtitle());
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

    private void sendRequestWithOkHttp(final Comments comments,final View view){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("session",session)
                            .add("id",String.valueOf(comments.getComment_id()))
                            .add("type","0")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/deleteMyNewsComment/")
                            .post(requestBody)
                            .addHeader("Connection","close")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    JSONTokener(responseDate);
                    JSONArray jsonArray = new JSONArray(responseDate);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    final int state = jsonObject.getInt("state");
                    String msg = jsonObject.getString("msg");

                    if (state == 1){
                        Toast.makeText(view.getContext(),"删除成功",Toast.LENGTH_LONG).show();
                    }else if (state == -1) {
                        Toast.makeText(view.getContext(), "您还没有登录，请先登录", Toast.LENGTH_LONG).show();
                    }else if (state == -2) {
                        Toast.makeText(view.getContext(), "删除失败", Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();

                    sendRequestWithOkHttp(comments,view);
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
}
