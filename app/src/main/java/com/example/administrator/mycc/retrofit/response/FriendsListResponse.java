package com.example.administrator.mycc.retrofit.response;

/**
 * @Author: obc
 * @Date: 2019/3/17 17:15
 * @Version 1.0
 */

import com.example.administrator.mycc.model.UserInfo;

import java.util.List;

/**
 * getFriendList的json解析Bean
 */
public class FriendsListResponse {

    private int code;

    private DataObj data;

    public FriendsListResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataObj getData() {
        return data;
    }

    public void setData(DataObj data) {
        this.data = data;
    }

    public static class DataObj {
        private String username;
        private List<UserInfo> friendList;

        public DataObj() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<UserInfo> getFriendList() {
            return friendList;
        }

        public void setFriendList(List<UserInfo> friendList) {
            this.friendList = friendList;
        }
    }
}
