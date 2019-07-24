package com.example.administrator.mycc.retrofit;

/**
 * @Author: obc
 * @Date: 2019/3/11 19:12
 * @Version 1.0
 */

import com.example.administrator.mycc.retrofit.response.RespObj;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 登录注册类接口
 */
public interface LoginService {

    @FormUrlEncoded
    @POST("api/signIn")
    Observable<RespObj> signIn(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/signUp")
    Observable<RespObj> signUp(@Field("username") String username, @Field("password") String password, @Field("nickname") String name);

    @FormUrlEncoded
    @POST("api/signOut")
    Observable<RespObj> signOut(@Field("username") String username);
}
