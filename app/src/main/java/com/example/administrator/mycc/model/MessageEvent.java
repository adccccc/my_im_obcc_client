package com.example.administrator.mycc.model;

/**
 * @Author: obc
 * @Date: 2019/3/21 15:59
 * @Version 1.0
 */

/**
 * EventBus传递的事件信息
 */
public class MessageEvent {

    public enum EventType {
        UPDATE_VIEW,     // 更新视图
        SEND_SINGLE_MESSAGE,     // 发送好友消息
        RECEIVE_SINGLE_MESSAGE,     // 收到好友消息
        AUTO_RECONNECT_FAILED       // 自动重连失败
    }
    private EventType type;
    public Object message;

    public MessageEvent(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
