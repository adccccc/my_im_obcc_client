package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/19 18:21
 * @Version 1.0
 */

/**
 * 群消息对应数据库实体
 */
public class GroupMessage extends BaseMessage{

    // 用户id
    private String username;

    // 消息所在群id
    private long groupId;

    public GroupMessage() {
    }

    public GroupMessage(String messageId, String username, long groupId, String senderId, long timestamp, int messageType, String content) {
        super(messageId, senderId, timestamp, messageType, content, false);
        this.username = username;
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
