package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/17 22:31
 * @Version 1.0
 */

/**
 * 好友消息数据库实体
 */
public class SingleMessage extends BaseMessage{

    // 用户id
    private String username;
    // 好友id
    private String friendUsername;

    public SingleMessage() {
    }

    public SingleMessage(String messageId, String username, String friendUsername, long timestamp, int messageType, String senderId, String content) {
        super(messageId, senderId, timestamp, messageType, content, true);
        this.username = username;
        this.friendUsername = friendUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }
}
