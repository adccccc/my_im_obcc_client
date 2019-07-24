package com.example.administrator.mycc.netty;

import com.example.administrator.mycc.model.MessageEvent;
import com.example.administrator.mycc.model.SingleMessage;
import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.service.SocketService;
import com.example.administrator.mycc.utils.PacketUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.greenrobot.eventbus.EventBus;

/**
 * @Author: obc
 * @Date: 2019/3/19 23:39
 * @Version 1.0
 */
public class SingleMessageHandler extends SimpleChannelInboundHandler<CcPacket.SingleChatPacket> {

    /**
     * 收到好友消息报文
     * @param channelHandlerContext
     * @param singleChatPacket
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CcPacket.SingleChatPacket singleChatPacket) throws Exception {
        // 回复ACK报文
        channelHandlerContext.channel().writeAndFlush(PacketUtils.generateAck(singleChatPacket.getMessageId(), null));


        SingleMessage message = PacketUtils.generateSingleMessage(singleChatPacket, true);

        System.out.println("收到消息:\n" + message.toString());
        MessageEvent event = new MessageEvent(MessageEvent.EventType.RECEIVE_SINGLE_MESSAGE);
        event.setMessage(message);
        EventBus.getDefault().post(event);

    }
}
