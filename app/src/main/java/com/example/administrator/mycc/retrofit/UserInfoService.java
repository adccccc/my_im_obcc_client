package com.example.administrator.mycc.retrofit;

/**
 * @Author: obc
 * @Date: 2019/3/15 14:20
 * @Version 1.0
 */

import com.example.administrator.mycc.retrofit.response.RespObj;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * 用户信息接口
 */
public interface UserInfoService {

    @GET("api/userInfo")
    Observable<RespObj> getUserInfo(@Query("username")String username);

}
