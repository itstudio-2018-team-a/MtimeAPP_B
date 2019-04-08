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
import com.example.lenovo.mtime.NewsComActivity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Comments;
import com.example.lenovo.mtime.bean.News;
import com.example.lenovo.mtime.bean.NewsCom;
import com.example.lenovo.mtime.register_Infor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsComAdapter extends RecyclerView.Adapter<NewsComAdapter.ViewHolder>{
    private List<NewsCom> list;
    private Context context;
    private String userName;
    private String session;
    private String newsId;

    public NewsComAdapter(List<NewsCom> list, String userName1, Context context,String newsId,String session){
        userName = userName1;
        this.list = list;
        this.context = context;
        this.newsId = newsId;
        this.session = session;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_author;
        TextView tv_author;
        TextView tv_context;
        View newsComView;

        public ViewHolder(View view) {
            super(view);
            newsComView = view;
            iv_author = (ImageView) view.findViewById(R.id.iv_author);
            tv_author = (TextView) view.findViewById(R.id.tv_author);
            tv_context = (TextView) view.findViewById(R.id.tv_content);
        }
    }

    public NewsComAdapter(List<NewsCom> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public NewsComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
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
                final NewsCom newsCom = list.get(position);
                String authorName = newsCom.getAuthor();

                if (userName == null) Toast.makeText(v .getContext(), "您还没有登录，请先登录", Toast.LENGTH_SHORT).show();
                else
                {
                    Snackbar.make(view,"确定要删除这条评论吗",Snackbar.LENGTH_LONG)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    sendRequestWithOkHttp(newsCom,view);
                                }
                            })
                            .show();
                }
                Log.d("评论者，用户",userName + authorName);
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsComAdapter.ViewHolder viewHolder, int i) {
        NewsCom newsCom = list.get(i);
        Glide.with(context).load("http://132.232.78.106:8001/media/"+newsCom.getAutherHeadPhoto()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        viewHolder.tv_author.setText(newsCom.getAuthor());
        viewHolder.tv_context.setText(newsCom.getContent());
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

    private void sendRequestWithOkHttp(final NewsCom newsCom,final View view){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("session",session)
                            .add("id",String.valueOf(newsCom.getId()))
                            .add("type","1")
                            .build();

                    Request request = new Request.Builder()
                            .url("http://132.232.78.106:8001/api/deleteMyNewsComment/")
                            .post(requestBody)
                            .addHeader("Connection","close")
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseDate = response.body().string();
                    Log.d("这个评论怎么搞的，返回的是啥",responseDate);
                    Log.d("这个评论怎么搞的，是id错了吗",String.valueOf(newsCom.getId()));
                    JSONTokener(responseDate);
                    JSONObject jsonObject = new JSONObject(responseDate);
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
