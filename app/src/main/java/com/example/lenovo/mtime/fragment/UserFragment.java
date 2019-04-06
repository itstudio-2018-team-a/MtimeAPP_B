package com.example.lenovo.mtime.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user,container,false);

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

        if(Login_Activity.flag == null){
            tv_userName.setText("未登录");

        }else {
            Bundle bundle = getArguments();
            if(bundle != null) {
                user_id = bundle.getString("user_id");
                String nickName = bundle.getString("nickName");
                String headImage = bundle.getString("headImage");
                String email = bundle.getString("email");
                session = bundle.getString("session");
             if (headImage.equals("default/1.png")){
                 user_image.setImageResource(R.drawable.user_128);//R.drawable.firstheadimage
             }else {
                 Glide.with(this).load(headImage).placeholder(R.drawable.user_128).error(R.drawable.user_128).into(user_image);
             }
                tv_userName.setText(nickName);
            }

        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Login_Activity.flag == null||Login_Activity.flag.equals("0")){
                    Intent intent = new Intent(getContext(), Login_Activity.class);
                    startActivity(intent);

                }else {
                          //弹出一个选择框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("您要从哪里选择新头像？");
                    builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }else
                                openAlbum();
                        }
                    });
                    builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File outputImage=new File(getActivity().getExternalCacheDir(),"output_image.jpg");
                            try{
                                if(outputImage.exists()){
                                    outputImage.delete();
                                }
                                outputImage.createNewFile();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            if(Build.VERSION.SDK_INT>=24){
                                imageUri= FileProvider.getUriForFile(getContext(),"com.example.a32936.fileprovider",outputImage);
                            }else{
                                imageUri= Uri.fromFile(outputImage);
                            }
                            //启动相机程序
                            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                            startActivityForResult(intent,TAKE_PHOTO);
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
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型","电影评论");
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("cookie",session);
                    startActivity(intent);
                }
            }
        });
        btn_newsComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getContext(), User_comments.class);
                    intent.putExtra("类型","新闻评论");
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {
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
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {

                Intent intent = new Intent(getContext(), ChangeName.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
            }
        });

        btn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Login_Activity.flag == null){
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }else {

                    Intent intent = new Intent(getContext(), ChangePassword.class);
                    intent.putExtra("user_id",user_id);
                    startActivity(intent);
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK){
                    try {
                        bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        Bitmap Bit=compressImage(bitmap);
                        user_image.setImageBitmap(Bit);
                        flag = 1;

                        uploadFile("outputImage",imageUri.toString());
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19)
                        handleImage(data);
                    else
                        handleBeforeImage(data);
                }
                break;
            default:break;
        }
    }
    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
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
        }
        catch (IOException e) {
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
        String imagePath = null;
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
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
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
            int width= options.outWidth;
            int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
            int minLen = Math.min(height, width); // 原图的最小边长
            if(minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
                inSampleSize = (int)ratio;
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


    public void uploadFile(String fileName , String filePath) {
        HttpURLConnection conn = null;
/// boundary就是request头和上传文件内容的分隔符(可自定义任意一组字符串)
        String BOUNDARY = "******";

        // 用来标识payLoad+文件流的起始位置和终止位置(相当于一个协议,告诉你从哪开始,从哪结束)
        String preFix = ("\r\n--" + BOUNDARY + "--\r\n");
        try {

// 上传到服务器

            URL url = new URL("http://132.232.78.106:8001/api/changeHeadImage/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);   //允许输出流
            conn.setDoInput(true);     //允许输入流
            conn.setUseCaches(false);     //网上说是不允许使用缓存的意思，具体我也不太了解
            // 设置请求方法
            conn.setRequestMethod("POST");
            // 设置header
            conn.setRequestProperty("Accept","*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
            //multipart/form-data  几部分的数据

            // 获取写输入流
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 获取上传文件
            File file = new File(filePath);
            // 要上传的数据
            StringBuffer strBuf = new StringBuffer();
            // 标识payLoad + 文件流的起始位置
            strBuf.append(preFix);
            // 下面这三行代码,用来标识服务器表单接收文件的name和filename的格式
            // 在这里,我们是file和filename.后缀[后缀是必须的]。
            // 这里的fileName必须加个.jpg,因为后台会判断这个东西。
            // 这里的Content-Type的类型,必须与fileName的后缀一致。
            // 这里只要把.jpg改成.txt，把Content-Type改成上传文本的类型，就能上传txt文件了。

            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName+".jpg" + "\"\r\n");
            strBuf.append("Content-Type: image/jpeg" + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            // 获取文件流
            FileInputStream fileInputStream = new FileInputStream(file);

            DataInputStream inputStream = new DataInputStream(fileInputStream);

            // 每次上传文件的大小(文件会被拆成几份上传)
            int bytes = 0;
            // 计算上传进度
            float count = 0;
            // 获取文件总大小
            int fileSize = fileInputStream.available();
            // 每次上传的大小
            byte[] bufferOut = new byte[1024];
            // 上传文件
            while ((bytes = inputStream.read(bufferOut)) != -1) {
                // 上传文件(一份)
                out.write(bufferOut, 0, bytes);
                // 计算当前已上传的大小
                count += bytes;
                // 打印上传文件进度(已上传除以总大*100就是进度)
                Log.d("hhh","progress:" +(count / fileSize * 100) +"%");
            }
            // 关闭文件流
            inputStream.close();
            // 标识payLoad + 文件流的结尾位置
            out.write(preFix.getBytes());
            // 至此上传代码完毕
            // 总结上传数据的流程：preFix + payLoad(标识服务器表单接收文件的格式) + 文件(以流的形式) + preFix
            // 文本与图片的不同,仅仅只在payLoad那一处的后缀的不同而已。
            // 输出所有数据到服务器
            out.flush();
            // 关闭网络输出流
            out.close();
            // 重新构造一个StringBuffer,用来存放从服务器获取到的数据
            strBuf = new StringBuffer();
            // 打开输入流 , 读取服务器返回的数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            // 一行一行的读取服务器返回的数据
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            // 关闭输入流
            reader.close();
            // 打印服务器返回的数据
            Log.d("hhh","上传成功:"+strBuf.toString());
        } catch (Exception e) {
            Log.d("hhh","上传图片出错:"+e.toString());
        } finally {
            if (conn != null) { conn.disconnect();
            }
        }
    }
}
