package com.example.administrator.mycc.model;

import java.util.Objects;

/**
 * @Author: obc
 * @Date: 2019/3/15 13:19
 * @Version 1.0
 */
public class UserInfo {

    // 用户名
    String username;
    // 昵称
    String nickname;
    // 头像编号
    Integer icon;


    public UserInfo(String username, String nickname, Integer icon) {
        this.username = username;
        this.nickname = nickname;
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return username.equals(userInfo.username) &&
                nickname.equals(userInfo.nickname) &&
                icon.intValue() == userInfo.icon.intValue();
    }

    @Override
    public int hashCode() {
        return username.hashCode() + nickname.hashCode() + icon.hashCode();
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", icon=" + icon +
                '}';
    }

    public UserInfo() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
