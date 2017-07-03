package com.test.qiuyu.imtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liujiabing on 2017/6/29.
 */

public class ImageScan extends Activity{
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_scan);
        imageView = (ImageView)findViewById(R.id.scanImageView);
        String uri = getIntent().getStringExtra("Uri");
        Log.d("qiuyu","ImageScan.Uri="+uri);
        if (!TextUtils.isEmpty(uri)){
            setBitmap(uri);
        }
    }

    private void setBitmap(final String path){
        AsyncTask<Object,Object,Bitmap> task = new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... objects) {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200){
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                Log.v("qiuyu","onPostExecute.bitmap="+bitmap);
                imageView.setImageBitmap(bitmap);
            }
        };
        task.execute();
    }
}
