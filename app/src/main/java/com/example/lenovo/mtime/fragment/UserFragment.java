package com.example.lenovo.mtime.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.mtime.ChangeName;
import com.example.lenovo.mtime.ChangePassword;
import com.example.lenovo.mtime.Login_Activity;
import com.example.lenovo.mtime.R;
import com.example.lenovo.mtime.User_comments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static com.example.lenovo.mtime.uitl.BitMaptoFile.getFile;

public class UserFragment extends Fragment {
    View view;
    CircleImageView user_image;
    RelativeLayout btn_login;
    Button btn_newsComments;
    Button btn_movieComments;
    LinearLayout btn_findPassword;
    LinearLayout btn_logout;
    LinearLayout btn_changePassword;
    LinearLayout btn_changeName;
    TextView tv_userName;
    String userName;
    private String user_id;
    private String url;
    private String session;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    public static Bitmap bitmap;
    public int flag;
    private byte[] pic;
    private String imagePath;
    private File outputImage;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        //绑监听
        user_image = (CircleImageView) view.findViewById(R.id.user_image);
        user_image.setImageResource(R.drawable.user_128);
        btn_login = (RelativeLayout) view.findViewById(R.id.btn_login);
        btn_movieComments = (Button) view.findViewById(R.id.btn_movieComments);
        btn_newsComments = (Button) view.findViewById(R.id.btn_newsComments);
        btn_logout = (LinearLayout) view.findViewById(R.id.btn_logout);
        btn_findPassword = (LinearLayout) view.findViewById(R.id.btn_findPassword);
        btn_changeName = (LinearLayout) view.findViewById(R.id.btn_changeName);
        btn_changePassword = (LinearLayout) view.findViewById(R.id.btn_changePassword);
        tv_userName = (TextView) view.findViewById(R.id.tv_userName);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
            tv_userName.setText("未登录");

        } else {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
            user_id = sharedPreferences.getString("user_id", "");
            String nickName = sharedPreferences.getString("nickName", "");
            String headImage = sharedPreferences.getString("headImage", "");
            String email = sharedPreferences.getString("email", "");
            session = sharedPreferences.getString("session", "");

            if (headImage.equals("default/1.png")) {
                user_image.setImageResource(R.drawable.firstheadimage);
            } else {
//                 headImage = "http://132.232.78.106:8001/media/"+headImage;
                Glide.with(this).load(headImage).placeholder(R.drawable.user_128).error(R.drawable.firstheadimage).into(user_image);
                editor = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                editor.putString("headImage", headImage);
                editor.apply();
            }
            tv_userName.setText(nickName);
//            }

        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Login_Activity.flag == null || Login_Activity.flag.equals("0")) {
                    Intent intent = new Intent(getContext(), Login_Activity.class);
                    startActivity(intent);

                } else {
                    //弹出一个选择框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("您要从哪里选择新头像？");
                    builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else
                                openAlbum();
                        }
                    });
                    builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                            try {
                                if (outputImage.exists()) {
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (Build.VERSION.SDK_INT >= 24) {
                                imageUri = FileProvider.getUriForFile(getContext(), "com.example.a32936.fileprovider", outputImage);
                            } else {
                                imageUri = Uri.fromFile(outputImage);
                            }
                            //启动相机程序
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, TAKE_PHOTO);
                        }
                    });
                    builder.setNeutralButton("算了，我不改了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);      //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                }

            }
        });
        btn_movieComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型", "电影评论");
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("cookie", session);
                    startActivity(intent);
                }
            }
        });
        btn_newsComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型", "新闻评论");
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    //弹出一个确认框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("您确定要退出登录吗？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "已取消", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), Login_Activity.class);
                            startActivity(intent);
                            Login_Activity.flag = "0";
                            user_image.setImageResource(R.drawable.user_128);
                            getActivity().finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(true);      //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                    dialog.show();
                }
            }
        });

        btn_changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(getContext(), ChangeName.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });

        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null||Login_Activity.flag.equals("0")) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(getContext(), ChangePassword.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap Bit = compressImage(bitmap);
                    final File file = getFile(Bit);
//                    user_image.setImageBitmap(Bit);
                    flag = 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient.Builder()
                                        .retryOnConnectionFailure(true)
                                        .connectTimeout(10, TimeUnit.SECONDS)
                                        .writeTimeout(10, TimeUnit.SECONDS)
                                        .readTimeout(10, TimeUnit.SECONDS)
                                        .build();

                                RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
                                RequestBody requestBody = new MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("headImage", "output_image.jpg", image)
                                        .addFormDataPart("session", session)
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://132.232.78.106:8001/api/changeHeadImage/")
                                        .post(requestBody)
                                        .build();

                                Call call = client.newCall(request);
                                Response response = null;
                                response = call.execute();
                                String responseData = response.body().string();


                                final JSONObject jsonObject = new JSONObject(responseData);
                                String state = jsonObject.getString("state");
                                final String msg = jsonObject.getString("msg");
                                if (state.equals("1")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                                String headImage = jsonObject.getString("imageHead");
                                                editor = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                                                editor.putString("headImage","http://132.232.78.106:8001/media/"+headImage);
                                                editor.apply();
                                                Glide.with(getContext()).load(headImage).placeholder(R.drawable.user_128).error(R.drawable.firstheadimage).into(user_image);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                            user_image.setImageResource(R.drawable.user_128);
                                        }
                                    });

                                }
                                Log.d("ZGH", responseData);


                            } catch (final Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        if (e instanceof SocketTimeoutException) {
                                            Toast.makeText(getContext(), "连接超时，请检查网络设置", Toast.LENGTH_SHORT).show();
                                        }
                                        if (e instanceof ConnectException) {
                                            Toast.makeText(getContext(), "连接异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                                        }

                                        if (e instanceof ProtocolException) {
                                            Toast.makeText(getContext(), "未知异常，请稍后再试", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImage(data);
                        uploadFile(imagePath);
                    } else {
                        handleBeforeImage(data);
                        uploadFile(imagePath);
                    }
                }
                break;
            default:
                break;
        }
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1000) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getContext(), "您拒绝了权限", Toast.LENGTH_LONG).show();
                }
                break;
            default:
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @TargetApi(19)
    private void handleImage(Intent data) {
        imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            // 通过document id来处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // 解析出数字id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 如果不是document类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }

        // 根据图片路径显示图片
        displayImage(imagePath);
        flag = 1;
    }

    private void handleBeforeImage(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
        flag = 1;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
            BitmapFactory.decodeFile(imagePath, options);
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
            int minLen = Math.min(height, width); // 原图的最小边长
            if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
                inSampleSize = (int) ratio;
            }
            options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
            options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
            Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
            pic = getBitmapByte(bm);
            user_image.setImageBitmap(bm);
            flag = 1;
        } else {
            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadFile(final String filePath) {

        Bitmap bitmap=BitmapFactory.decodeFile(filePath);
        bitmap = compressImage(bitmap);
        final File file = getFile(bitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();

                RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("headImage", filePath, image)
                        .addFormDataPart("session", session)
                        .build();

                Request request = new Request.Builder()
                        .url("http://132.232.78.106:8001/api/changeHeadImage/")
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);

                try {
                    Response response = call.execute();

                    String responseData = response.body().string();

                    final JSONObject jsonObject = new JSONObject(responseData);
                    String state = jsonObject.getString("state");
                    final String msg = jsonObject.getString("msg");
                    if (state.equals("1")) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                String headImage = null;

                                    headImage = jsonObject.getString("imageHead");

                                editor = getActivity().getSharedPreferences("data",Context.MODE_PRIVATE).edit();
                                editor.putString("headImage","http://132.232.78.106:8001/media/"+headImage);
                                editor.apply();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                user_image.setImageResource(R.drawable.user_128);
                            }
                        });

                    }

                } catch (final Exception e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            if (e instanceof SocketTimeoutException) {
                                Toast.makeText(getContext(), "连接超时", Toast.LENGTH_SHORT).show();
                            }
                            if (e instanceof ConnectException) {
                                Toast.makeText(getContext(), "连接异常", Toast.LENGTH_SHORT).show();
                            }

                            if (e instanceof ProtocolException) {
                                Toast.makeText(getContext(), "未知异常，请稍后再试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        }).start();
    }


}
