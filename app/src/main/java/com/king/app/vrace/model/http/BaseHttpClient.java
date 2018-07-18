package com.king.app.vrace.model.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/9/1.
 */
public abstract class BaseHttpClient {

    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;

    public BaseHttpClient() {
        createRetrofit();
    }

    private void createRetrofit() {
        // 查看通信LOG，可以输入OkHttp来过滤
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置连接超时
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        // 设置log打印器
        builder.addInterceptor(loggingInterceptor);

        extendBuilder(builder);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(builder.build())
                .build();

        createService(retrofit);
    }

    protected abstract void extendBuilder(OkHttpClient.Builder builder);

    protected abstract void createService(Retrofit retrofit);

}
