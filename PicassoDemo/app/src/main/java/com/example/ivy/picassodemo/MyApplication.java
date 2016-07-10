package com.example.ivy.picassodemo;

import android.app.Application;
import android.graphics.Bitmap;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

/**
 * Created by Ivy on 2016/7/10.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        initOkHttp();
        initPicasso();
    }

    private void initOkHttp() {
        OkHttpUtils utils = OkHttpUtils.getOkHttpUtils(this);
    }

    private void initPicasso() {
        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache(10 << 20))
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .downloader(new MyOkHttpDownloader(this.getCacheDir(),10<<20))
                .indicatorsEnabled(true)
                .build();
        //设置图片左上角的标记
        //红色：代表从网络下载的图片
        //蓝色：代表从磁盘缓存加载的图片
        //绿色：代表从内存中加载的图片

        Picasso.setSingletonInstance(picasso);
    }
}
