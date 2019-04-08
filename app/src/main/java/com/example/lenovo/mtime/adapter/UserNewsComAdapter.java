package com.example.lenovo.mtime.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.bean.UserMovCom;
import com.example.lenovo.mtime.bean.UserNewsCom;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserNewsComAdapter extends RecyclerView.Adapter<UserNewsComAdapter.ViewHolder>{
    private List<UserNewsCom> newsComslist;
    private Context context;
    public String user_id;
    private String session;
    private String type;   //用于判断显示的是新闻评论还是电影评论
    private String id;

    public UserNewsComAdapter(List<UserNewsCom> list, String user_id, Context context){
        this.user_id = user_id;
        this.newsComslist = list;
        this.context = context;

    }
    @NonNull
    @Override
    public UserNewsComAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null){
            context = viewGroup.getContext();
        }
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.item_user_newscom, viewGroup, false);
        final UserNewsComAdapter.ViewHolder holder = new UserNewsComAdapter.ViewHolder(view);
        holder.commentview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("您确定要删除这条评论吗？");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendRequestWithokHttp();
                    }
                });
                builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);      //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                dialog.show();

                return true;
            }
        });
        return null;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull UserNewsComAdapter.ViewHolder viewHolder, int i) {
        UserNewsCom userNewsCom = newsComslist.get(i);
        id = userNewsCom.getId();
        viewHolder.tv_movieTitle.setText(userNewsCom.getnewsTitle());
        viewHolder.tv_time.setText(userNewsCom.getcreate_time());
        viewHolder.tv_content.setText(userNewsCom.getContent());


    }

    @Override
    public int getItemCount() {
        if(null==newsComslist) return 0;
        else return newsComslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_movieTitle;
        TextView tv_content;
        View commentview;
        TextView tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentview = itemView;
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_movieTitle = itemView.findViewById(R.id.tv_newsTitle);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }

    public void sendRequestWithokHttp(){
        //开启现线程发起网络请求
        new Thread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(){

                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20,TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id",id)
                        .addFormDataPart("session",session)
                        .addFormDataPart("type","0")
                        .build();

                Request request = new Request.Builder()
                        .url("http://132.232.78.106:8001/api/deleteMyNewsComment/")   //网址有待改动
                        .post(requestBody)
                        .addHeader("Connection","close")
//                            .addHeader("cookie",cookie)
                        .build();

                Call call = client.newCall(request);
                String responseDate = "";
                try{
                    Response response = call.execute();
                    responseDate = response.body().string();
                    Log.d("ZGH",responseDate);
                    JSONObject jsonObject = new JSONObject(responseDate);
                    String state = jsonObject.getString("state");
                    String msg = jsonObject.getString("msg");
                    Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();

                }catch (final Exception e){
                    Log.e("ZGHhh", responseDate);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    e.printStackTrace();
                    if (e instanceof SocketTimeoutException){
                        Toast.makeText(context,"连接超时",Toast.LENGTH_SHORT).show();
                    }
                    if (e instanceof ConnectException){
                        Toast.makeText(context,"连接异常",Toast.LENGTH_SHORT).show();
                    }

                    if (e instanceof ProtocolException) {
                        Toast.makeText(context,"未知异常，请稍后再试",Toast.LENGTH_SHORT).show();
                    }
                }
//                    });

            }
//            }
        }).start();
    }
}
