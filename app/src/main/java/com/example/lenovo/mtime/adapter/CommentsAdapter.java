package com.example.lenovo.mtime.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.CommentsDetail;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.Comments;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

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
    private int normalType = 0;
    private int footType = 1;

    private boolean hasMore = true;   // 变量，是否有更多数据
    private boolean fadeTips = false; // 变量，是否隐藏了底部的提示
    //private Handler mHandler = new Handler(Looper.getMainLooper());

    public CommentsAdapter(List<Comments> list, String user_id, Context context,String session,boolean hasMore){
        this.session = session;
        this.user_id = user_id;
        this.list = list;
        this.context = context;
        this.hasMore = hasMore;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    // 自定义方法，获取列表中数据源的最后一个位置，比getItemCount少1，因为不计上footView
    public int getRealLastPosition() {
        return list.size();
    }
    // 根据条目位置返回ViewType，以供onCreateViewHolder方法内获取不同的Holder
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footType;
        } else {
            return normalType;
        }
    }
    // 正常item的ViewHolder，用以缓存findView操作
    class NormalHolder extends CommentsAdapter.ViewHolder {
        private LinearLayout textView;

        public NormalHolder(View itemView) {
            super(itemView);
            textView = (LinearLayout) itemView.findViewById(R.id.tv);
        }
    }

    // // 底部footView的ViewHolder，用以缓存findView操作
    class FootHolder extends CommentsAdapter.ViewHolder {
        private TextView tips;

        public FootHolder(View itemView) {
            super(itemView);
            tips = (TextView) itemView.findViewById(R.id.tips);
        }
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
        // 根据返回的ViewType，绑定不同的布局文件，这里只有两种

        if (context == null){
            context = viewGroup.getContext();
        }
        final View view;
        if (i == normalType) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_comments, viewGroup, false);
        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.footview, viewGroup, false);
        }


        final CommentsAdapter.ViewHolder holder = new CommentsAdapter.ViewHolder(view);
        holder.commentsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Comments comments = list.get(position);
                String commentId = String.valueOf(comments.getComment_id());
                if(session == null) Toast.makeText(view.getContext(),"您还未登录，请先登录",Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent();
                    intent.setClass(view .getContext(), CommentsDetail.class );
                    intent.putExtra("commentsId", commentId);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("session",session);
                    intent.putExtra("userImg",comments.getAuthor_head());
                    intent.putExtra("poster",comments.getPoster());
                    view.getContext().startActivity(intent);
                }
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
        if (i == normalType) {
            return new NormalHolder(LayoutInflater.from(context).inflate(R.layout.item_comments, null));
        } else {
            return new FootHolder(LayoutInflater.from(context).inflate(R.layout.footview, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder viewHolder, int i) {
        Comments comments = list.get(i);
        //viewHolder.tv_movieTitle.setText(comments.get);

        //原始方法
        //Log.d("hhh",comments.getAuthor_head());
        //Glide.with(context).load("http://132.232.78.106:8001"+comments.getPoster()).placeholder(R.drawable.eg).error(R.drawable.code_128).into(viewHolder.iv_movie);
        //Glide.with(context).load("http://132.232.78.106:8001"+comments.getAuthor_head()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        //viewHolder.tv_commentsAuthor.setText(comments.getAuthor_name());
        //viewHolder.tv_commentsTitle.setText(comments.getTitle());
        //viewHolder.tv_summary.setText("“"+comments.getSubtitle()+"”");
        //上拉加载
        // 如果是正常的imte，直接设置TextView的值
        if (viewHolder instanceof NormalHolder) {
            ((NormalHolder) viewHolder).tv_commentsAuthor.setText(list.get(i).getAuthor_name());
            ((NormalHolder) viewHolder).tv_commentsTitle.setText(list.get(i).getTitle());
            ((NormalHolder) viewHolder).tv_summary.setText("“"+list.get(i).getSubtitle()+"”");
            Glide.with(context).load("http://132.232.78.106:8001"+list.get(i).getPoster()).placeholder(R.drawable.eg).error(R.drawable.code_128).into(viewHolder.iv_movie);
            Glide.with(context).load("http://132.232.78.106:8001"+list.get(i).getAuthor_head()).placeholder(R.drawable.eg).error(R.drawable.eg).into(viewHolder.iv_author);
        } else {
            // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
            ((FootHolder) viewHolder).tips.setVisibility(View.VISIBLE);
            // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
            if (hasMore == true) {
                // 不隐藏footView提示
                fadeTips = false;
                if (list.size() > 0) {
                    // 如果查询数据发现增加之后，就显示正在加载更多
                    ((FootHolder) viewHolder).tips.setText("正在加载更多...");
                }
            } else {
                if (list.size() > 0) {
                    // 如果查询数据发现并没有增加时，就显示没有更多数据了
                    ((FootHolder) viewHolder).tips.setText("没有更多数据了");

                    // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                    //mHandler.postDelayed(new Runnable() {
                        //@Override
                        //public void run() {
                            // 隐藏提示条
                          //  ((FootHolder) viewHolder).tips.setVisibility(View.GONE);
                            // 将fadeTips设置true
                           // fadeTips = true;
                            // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                          //  hasMore = true;
                       // }
                    //}, 500);
                }
            }
        }
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
//                    sendRequestWithOkHttp(comments,view);
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

    public void resetDatas() {
        list = new ArrayList<>();
    }
    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<Comments> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            list.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }
    // 暴露接口，改变fadeTips的方法
    public boolean isFadeTips() {
        return fadeTips;
    }



}
