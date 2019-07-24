package com.example.administrator.mycc.retrofit;

/**
 * @Author: obc
 * @Date: 2019/3/17 16:06
 * @Version 1.0
 */

import com.example.administrator.mycc.retrofit.response.FriendsListResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 好友信息接口
 */
public interface FriendService {

    @GET("api/friendList")
    Observable<FriendsListResponse> getFriendsList(@Query("username") String username);
}
