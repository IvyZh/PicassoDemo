package com.example.ivy.picassodemo;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ivy on 2016/7/10.
 */
public class OkHttpUtils {
    private final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C)";
    private static OkHttpClient client;
    private static OkHttpUtils clientUtils;


    public OkHttpUtils(Context ctx) {
        client = getClientIntance();
        //  设置User-Agent


        // 开启响应缓存

        client.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));

        // 设置缓存目录和大小

        int cacheSize = 10 << 20;//10mb
        Cache cache = new Cache(ctx.getCacheDir(), cacheSize);
        client.setCache(cache);

        // 设置合理超时

        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(20, TimeUnit.SECONDS);
        client.setWriteTimeout(20, TimeUnit.SECONDS);
    }

    public static OkHttpClient getClientIntance() {
        if (client == null) {

            synchronized (OkHttpUtils.class) {
                if (client == null) {
                    client = new OkHttpClient();
                }
            }
        }
        return client;
    }

    public static OkHttpUtils getOkHttpUtils(Context ctx) {

        if (clientUtils == null) {

            synchronized (OkHttpUtils.class) {
                if (clientUtils == null) {
                    clientUtils = new OkHttpUtils(ctx);
                }
            }
        }
        return clientUtils;
    }


    // post同步请求
    public static String post(Context ctx, Map<String, String> params, String url, Object tag) {
        Request request = getOkHttpUtils(ctx).buildRequest(params, url, tag);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    // post异步请求
    public static void postAsync(Context ctx, Map<String, String> params, String url, Object tag, Callback callback) {
        Request request = getOkHttpUtils(ctx).buildRequest(params, url, tag);
        client.newCall(request).enqueue(callback);
    }


    private Request buildRequest(Map<String, String> params, String url, Object tag) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            builder.add(key, value);
        }

        RequestBody requestBody = builder.build();

        return new Request.Builder().url(url).tag(tag).post(requestBody).build();
    }

    // get同步请求

    public static String get(Context ctx, String url, Object tag) {
        Request request = getOkHttpUtils(ctx).buildGetRequest(url, tag);
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    // get 异步网络请求

    public static void getAsync(Context ctx, String url, Object tag, Callback callback) {
        Request request = getOkHttpUtils(ctx).buildGetRequest(url, tag);
        client.newCall(request).enqueue(callback);
    }


    private Request buildGetRequest(String url, Object tag) {
        return new Request.Builder().url(url).tag(tag).build();
    }


    // post 请求提交Json数据-同步

    public static String postJson(Context ctx, String url, String json, Object tag) {
        String JSON_TYPE = "application/json;charset=utf-8";
        RequestBody requestBody = RequestBody.create(MediaType.parse(JSON_TYPE), json);

        Request request = new Request.Builder().post(requestBody).url(url).tag(tag).build();
        Response response = null;
        try {
            response = getClientIntance().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // post 请求提交Json数据-异步

    public static void postJsonAsync(Context ctx, String url, String json, Object tag, Callback callback) {
        String JSON_TYPE = "application/json;charset=utf-8";
        RequestBody requestBody = RequestBody.create(MediaType.parse(JSON_TYPE), json);
        Request request = new Request.Builder().post(requestBody).url(url).tag(tag).build();
        Response response = null;
        getClientIntance().newCall(request).enqueue(callback);
    }


    /**
     * 上传文件
     *
     * @param
     */
    public static String postUploadFiles(Context ctx, String url, Map<String, String> map, File[] files, String[] formFieldName, Object tag) {

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));

            }
        }

        if (files != null && formFieldName != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String formName = formFieldName[i];
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), files[i]);

                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + formName + "\"; filename=\"" + fileName + "\""),
                        requestBody
                );
            }
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().tag(tag).url(url).post(requestBody).build();

        Response response = null;
        try {
            response = getClientIntance().newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void postUploadFilesAsync(Context ctx, String url, Map<String, String> map, File[] files, String[] formFieldName, Object tag, Callback callback) {

        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                        RequestBody.create(null, entry.getValue()));

            }
        }

        if (files != null && formFieldName != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                String formName = formFieldName[i];
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), files[i]);

                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + formName + "\"; filename=\"" + fileName + "\""),
                        requestBody
                );
            }
        }

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().tag(tag).url(url).post(requestBody).build();


        getClientIntance().newCall(request).enqueue(callback);

    }

    public void cancelCall(Context ctx, Object tag) {
        getClientIntance().cancel(tag);
    }

}
