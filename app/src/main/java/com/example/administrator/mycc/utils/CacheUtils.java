package com.example.administrator.mycc.utils;

import com.example.administrator.mycc.model.*;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: obc
 * @Date: 2019/3/17 14:08
 * @Version 1.0
 */

public class CacheUtils {

    // 好友列表
    private List<String> friendsList;

    // 用户信息map(username --> userInfo)
    private Map<String, UserInfo> userInfoMap;

    // 登录id与token
    private LoginInfo loginInfo;

    // 好友消息记录Map (friendId --> messages)
    private Map<String, List<BaseMessage>> singleMessageMap;

    // 群消息记录Map (groupId --> messages)
    private Map<Long, List<BaseMessage>> groupMessageMap;

    // 聊天会话列表
    private List<ChatTitle> chatTitleList;

    // TCP连接句柄
    private Channel tcpChannel;

    // TCP重连标记，登出时设置为false;
    private boolean reconnectFlag;

    private static volatile CacheUtils instance = null;

    private CacheUtils(){
        clearCache();
    }

    public static CacheUtils getInstance() {
        if (instance == null) {
            synchronized (CacheUtils.class) {
                if (instance == null) {
                    instance = new CacheUtils();
                }
            }
        }
        return instance;
    }

    public void clearCache() {
        friendsList = new ArrayList<>();
        userInfoMap = new ConcurrentHashMap<>();
        singleMessageMap = new ConcurrentHashMap<>();
        groupMessageMap = new ConcurrentHashMap<>();
        chatTitleList = new ArrayList<>();
        loginInfo = null;
        tcpChannel = null;
        reconnectFlag = false;
    }

    public List<String> getFriendsList() {
        return friendsList;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public Map<String, UserInfo> getUserInfoMap() {
        return userInfoMap;
    }

    public Map<String, List<BaseMessage>> getSingleMessageMap() {
        return singleMessageMap;
    }

    public Map<Long, List<BaseMessage>> getGroupMessageMap() {
        return groupMessageMap;
    }

    public List<ChatTitle> getChatTitleList() {
        return chatTitleList;
    }

    public Channel getTcpChannel() {
        return tcpChannel;
    }

    public void setTcpChannel(Channel tcpChannel) {
        this.tcpChannel = tcpChannel;
    }

    public boolean isReconnectFlag() {
        return reconnectFlag;
    }

    public void setReconnectFlag(boolean reconnectFlag) {
        this.reconnectFlag = reconnectFlag;
    }
}
