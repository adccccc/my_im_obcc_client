package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/19 18:27
 * @Version 1.0
 */

/**
 * MessageList填充的消息实体
 */
public class BaseMessage {

    // 消息id
    private String messageId;
    // 发送者id
    private String senderId;
    // 消息时间戳
    private long timestamp;
    // 消息类型 0-文字 1-图片 2-其他
    private int messageType;
    // 消息内容
    private String content;
    // true-好友消息  false-群消息
    private boolean singleMessage;

    public BaseMessage() {
    }

    public BaseMessage(String messageId, String senderId, long timestamp, int messageType, String content, boolean singleMessage) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.content = content;
        this.singleMessage = singleMessage;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSingleMessage() {
        return singleMessage;
    }

    public void setSingleMessage(boolean singleMessage) {
        this.singleMessage = singleMessage;
    }

    @Override
    public String toString() {
        return "[messageId: " + messageId + "\n"
                + "senderId: " + senderId + "\n"
                + "timestamp: " + timestamp + "\n"
                + "messageType: " + messageType + "\n"
                + "content: " + content + "]";
    }
}
