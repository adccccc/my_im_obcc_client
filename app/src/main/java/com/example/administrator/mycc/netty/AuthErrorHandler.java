package com.example.administrator.mycc.netty;

import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.service.SocketService;
import com.example.administrator.mycc.utils.CacheUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: obc
 * @Date: 2019/3/20 17:54
 * @Version 1.0
 */

/**
 * Tcp连接认证失败响应报文处理
 */
public class AuthErrorHandler extends SimpleChannelInboundHandler<CcPacket.AuthErrorPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CcPacket.AuthErrorPacket authErrorPacket) throws Exception {
        // token验证失败，需要重新登录
        System.out.println(authErrorPacket.getContent());
        CacheUtils.getInstance().setReconnectFlag(false);
    }
}
