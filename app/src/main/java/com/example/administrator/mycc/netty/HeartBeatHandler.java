package com.example.administrator.mycc.netty;

import com.example.administrator.mycc.utils.ConstantUtils;
import com.example.administrator.mycc.utils.PacketUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author: obc
 * @Date: 2019/3/21 21:12
 * @Version 1.0
 */

/**
 * 定期发送心跳包
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduleSendHeartBeat(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    private void scheduleSendHeartBeat(final ChannelHandlerContext ctx) {
        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(PacketUtils.generateHeartBeatPacket());
                    scheduleSendHeartBeat(ctx);
                }
            }
        }, ConstantUtils.HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
