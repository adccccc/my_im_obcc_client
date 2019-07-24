package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/18 12:41
 * @Version 1.0
 */


import android.support.annotation.NonNull;

/**
 * 聊天列表item的实体
 */
public class ChatTitle implements Comparable<ChatTitle>{

    // 聊天类型  0-好友聊天  1-群聊天  2-待拓展
    private int chatType;

    // 好友id或群id
    private String id;

    // 时间戳
    private long timestamp;

    // 最新一条消息内容
    private String content;

    // 未读消息数量
    private int msgCount;

    public ChatTitle(int chatType, String id, long timestamp, String content, int msgCount) {
        this.chatType = chatType;
        this.id = id;
        this.timestamp = timestamp;
        this.content = content;
        this.msgCount = msgCount;
    }

    public ChatTitle() {
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    // 根据时间戳降序排序
    @Override
    public int compareTo(@NonNull ChatTitle chatTitle) {
        if (this.getTimestamp() - chatTitle.getTimestamp() > 0) {
            return -1;
        } else if (this.getTimestamp() - chatTitle.getTimestamp() < 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
