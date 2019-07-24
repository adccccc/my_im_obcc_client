package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/17 14:13
 * @Version 1.0
 */
public class LoginInfo {

    private String username;

    private String token;

    public LoginInfo() {
    }

    public LoginInfo(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
