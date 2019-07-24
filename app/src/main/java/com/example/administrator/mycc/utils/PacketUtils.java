package com.example.administrator.mycc.utils;

/**
 * @Author: obc
 * @Date: 2019/3/20 15:43
 * @Version 1.0
 */

import com.example.administrator.mycc.model.SingleMessage;
import com.example.administrator.mycc.proto.CcPacket;

import java.util.Date;

/**
 * 报文生成
 */
public class PacketUtils {
    /**
     * 生成通用Ack报文
     * @param messageId
     * @param content
     * @return
     */
    public static CcPacket.AckPacket generateAck(String messageId, String content) {
        CcPacket.AckPacket.Builder builder = CcPacket.AckPacket.newBuilder();
        builder.setAckId(messageId)
                .setTimestamp(new Date().getTime())
                .setContent("");
        return builder.build();
    }

    /**
     * 生成SingleMessage
     * @param singleChatPacket
     * @Param isReceiver 是否为消息接收方
     * @return
     */
    public static SingleMessage generateSingleMessage(CcPacket.SingleChatPacket singleChatPacket, boolean isReceiver) {
        SingleMessage message = new SingleMessage();
        message.setMessageId(singleChatPacket.getMessageId());
        if (isReceiver) {       // 接收的消息
            message.setUsername(singleChatPacket.getReceiverId());
            message.setFriendUsername(singleChatPacket.getSenderId());
        } else {                // 发送的消息
            message.setUsername(singleChatPacket.getSenderId());
            message.setFriendUsername(singleChatPacket.getReceiverId());
        }
        message.setTimestamp(singleChatPacket.getTimestamp());
        message.setMessageType(singleChatPacket.getType().getNumber());
        message.setSenderId(singleChatPacket.getSenderId());
        message.setContent(singleChatPacket.getContent());
        return message;
    }

    public static CcPacket.HeartBeatPacket generateHeartBeatPacket() {
        return CcPacket.HeartBeatPacket.newBuilder()
                .setMessageId(MsgIdUtils.getUUID())
                .build();
    }
}
