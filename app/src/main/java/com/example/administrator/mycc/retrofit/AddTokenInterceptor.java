package com.example.administrator.mycc.retrofit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @Author: obc
 * @Date: 2019/3/15 14:43
 * @Version 1.0
 */
public class AddTokenInterceptor implements Interceptor {

    private String token;

    public AddTokenInterceptor(String token) {
        this.token = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .addHeader("Cookie", "token="+token)
                .addHeader("token", token);
        return chain.proceed(builder.build());
    }
}
