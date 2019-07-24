package com.example.administrator.mycc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import com.example.administrator.mycc.dao.ChatTitleDao;
import com.example.administrator.mycc.dao.MessageDao;
import com.example.administrator.mycc.model.BaseMessage;
import com.example.administrator.mycc.model.ChatTitle;
import com.example.administrator.mycc.model.MessageEvent;
import com.example.administrator.mycc.model.SingleMessage;
import com.example.administrator.mycc.others.MyThreadPoolManager;
import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.thread.NettyClientThread;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.PacketUtils;
import io.netty.channel.Channel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: obc
 * @Date: 2019/3/16 13:18
 * @Version 1.0
 */

/**
 * Socket连接服务端保持通信
 */
public class SocketService extends Service {

    // 线程同步锁对象
    private static final Object lockObj = new Object();

    String username;
    String token;
    NettyClientThread thread;
    Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        username = CacheUtils.getInstance().getLoginInfo().getUsername();
        token = CacheUtils.getInstance().getLoginInfo().getToken();
        thread = new NettyClientThread(username, token);
        context = getApplicationContext();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reconnectTcp();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务关闭时，断开TCP连接
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        Channel channel = CacheUtils.getInstance().getTcpChannel();
        if (channel != null && channel.isActive()) {
            channel.close();
            // 关闭重连
            CacheUtils.getInstance().setReconnectFlag(false);
        }

        EventBus.getDefault().unregister(this);
    }

    /**
     * 重新连接TCP
     */
    public void reconnectTcp() {
        // 设置重连标志为true
        CacheUtils.getInstance().setReconnectFlag(true);

        Channel channel = CacheUtils.getInstance().getTcpChannel();
        // 在channel不存在或已经断开时，新建TCP连接线程
        if (channel == null || !channel.isActive()) {
            MyThreadPoolManager.getInstance().execute(thread);
        }
    }

    /**
     * 收到好友消息
     * @param message
     */
    public void onReceivedSingleMessage(final SingleMessage message) {
        final String sender = message.getFriendUsername();

        System.out.println("收到信息:\n" + message.toString());

        MyThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 子线程将message写入db，如果为重复消息则写入失败
                long rid = MessageDao.getInstance(context).insertSingleMessage(message);
                if (rid < 0) return;        // 重复消息无需处理

                // 为了保证线程安全，将后续操作全部锁住
                // TODO ： 降低锁粒度
                synchronized (lockObj) {
                    List<BaseMessage> messageList = CacheUtils.getInstance().getSingleMessageMap().get(sender);
                    if (messageList == null) {   // 此好友消息列表为null，需要从db加载
                        messageList = MessageDao.getInstance(context).getSingleMessageList(username, sender);
                        CacheUtils.getInstance().getSingleMessageMap().put(sender, messageList);
                    }
                    // 将新消息添加到列表中
                    messageList.add(message);

                    // 更新外部聊天列表内容
                    ChatTitle chatTitleToDb = null;
                    List<ChatTitle> titleList = CacheUtils.getInstance().getChatTitleList();
                    boolean flag = false;
                    for (ChatTitle title : titleList) {
                        if (title.getId().equals(sender)) {
                            title.setMsgCount(title.getMsgCount() + 1);
                            title.setContent(message.getContent());
                            title.setTimestamp(message.getTimestamp());
                            flag = true;
                            chatTitleToDb = title;
                            break;
                        }
                    }
                    if (!flag) {        // 新建title
                        ChatTitle title = new ChatTitle(0, message.getFriendUsername(), message.getTimestamp(), message.getContent(), 1);
                        titleList.add(title);
                        chatTitleToDb = title;
                    }
                    // 更新db
                    ChatTitleDao.getInstance(context).insertOrUpdateChatTitle(username, chatTitleToDb);

                    // 重新排序
                    // Todo : 更改数据结构， 解决线程安全问题
                    Collections.sort(titleList);

                    System.out.println("信息处理完成");

                    // eventBus向chatFragment/chatActivity传递更新视图事件
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType.UPDATE_VIEW));
                }
            }
        });

    }


    /**
     * 消息中转站， 基本上所有事件的处理都在这里进行
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getType()) {
            case SEND_SINGLE_MESSAGE:       // 发送好友消息
                CcPacket.SingleChatPacket packet = (CcPacket.SingleChatPacket)event.getMessage();
                CacheUtils.getInstance().getTcpChannel().writeAndFlush(packet);
                SingleMessage message = PacketUtils.generateSingleMessage(packet, false);
                onReceivedSingleMessage(message);
                break;

            case RECEIVE_SINGLE_MESSAGE:        // 收到好友消息
                SingleMessage messageReceived = (SingleMessage)event.getMessage();
                onReceivedSingleMessage(messageReceived);
                break;

            case AUTO_RECONNECT_FAILED:         // 自动重连失败
                break;

            default:
                break;
        }
    }

}
