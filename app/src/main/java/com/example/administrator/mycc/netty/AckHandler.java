package com.example.administrator.mycc.netty;

import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.service.SocketService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: obc
 * @Date: 2019/3/16 14:29
 * @Version 1.0
 */
public class AckHandler extends SimpleChannelInboundHandler<CcPacket.AckPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CcPacket.AckPacket ackPacket) throws Exception {
        System.out.println("received Ack message： " + ackPacket.getAckId());
    }
}
