package com.example.lenovo.mtime.uitl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lenovo.mtime.ChangeName;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class okhttp_plus {
    public static String responseDate = "";
    public static String sendRequestwithOkHttp(final String url, final RequestBody requestBody, String user_id, final Context context) {
        //开启现线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)  //网查解决end of the stream问题
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .build();

//                    RequestBody requestBody = new MultipartBody.Builder()
//                            .setType(MultipartBody.FORM)
////                            .addFormDataPart("nickName",name)
////                            .addFormDataPart("session",session)
//                            .build();
                    Request request = new Request.Builder()
                            .post(requestBody)
                            .url(url)   //网址有待改动
                            .build();

                    Call call = client.newCall(request);
                    responseDate = "";
                    try {
                        Response response = call.execute();
                        responseDate = response.body().string();
                        Log.d("ZGH", "这是工具类里面的responsedata"+responseDate);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
                        }
                        if (e instanceof ConnectException) {
                            Toast.makeText(context, "连接异常", Toast.LENGTH_SHORT).show();
                        }

                        if (e instanceof ProtocolException) {
                            Toast.makeText(context, "未知异常，请稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return responseDate;
    }

//    private void showResponse(final String msg,Context context){
//
//                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
//
//    }
}
