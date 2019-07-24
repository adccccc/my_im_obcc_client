package com.example.administrator.mycc.thread;

/**
 * @Author: obc
 * @Date: 2019/3/16 13:49
 * @Version 1.0
 */

import com.example.administrator.mycc.model.MessageEvent;
import com.example.administrator.mycc.netty.AckHandler;
import com.example.administrator.mycc.netty.AuthErrorHandler;
import com.example.administrator.mycc.netty.HeartBeatHandler;
import com.example.administrator.mycc.netty.SingleMessageHandler;
import com.example.administrator.mycc.proto.CcPacket;
import com.example.administrator.mycc.proto.CustomProtobufDecoder;
import com.example.administrator.mycc.proto.CustomProtobufEncoder;
import com.example.administrator.mycc.service.SocketService;
import com.example.administrator.mycc.utils.CacheUtils;
import com.example.administrator.mycc.utils.ConstantUtils;
import com.example.administrator.mycc.utils.MsgIdUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.greenrobot.eventbus.EventBus;

/**
 * netty 客户端
 */
public class NettyClientThread extends Thread {

    private final String host = ConstantUtils.TCP_HOST;
    private final int port = ConstantUtils.TCP_PORT;

    String username;
    String token;
    int reconnectCount;


    public NettyClientThread(String username, String token) {
        this.username = username;
        this.token = token;
        this.reconnectCount = 0;
    }

    @Override
    public void run() {
        while (reconnectCount < 3) {
            try {
                connect();
            } catch (Exception e) {     // 网络断开、服务端强制关闭TCP连接等原因
                e.printStackTrace();
                System.out.println("TCP连接异常");
            }
            reconnectCount++;

            // 延迟2s再进行重连
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 用户已登出 或 token校验失败 则结束线程，否则重连
            if (!CacheUtils.getInstance().isReconnectFlag()) {
                return;
            }

        }

        // 重连失败超过两次，结束线程并UI提示
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType.AUTO_RECONNECT_FAILED));
    }

    private void connect() throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ConstantUtils.TCP_CONNECT_TIME_OUT)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new CustomProtobufDecoder());
                            socketChannel.pipeline().addLast("encoder", new CustomProtobufEncoder());
                            socketChannel.pipeline().addLast(new AckHandler());
                            socketChannel.pipeline().addLast(new SingleMessageHandler());
                            socketChannel.pipeline().addLast(new AuthErrorHandler());
                            socketChannel.pipeline().addLast(new HeartBeatHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            CacheUtils.getInstance().setTcpChannel(future.channel());
            reconnectCount = 0;

            CcPacket.AuthPacket.Builder builder = CcPacket.AuthPacket.newBuilder();
            CcPacket.AuthPacket packet = builder.setUserId(username)
                    .setToken(token)
                    .setMessageId(MsgIdUtils.getUUID()).build();
            future.channel().writeAndFlush(packet);

            // 等待channel正常关闭
            // 三种情形: 1-客户端主动登出关闭连接(无需重连)  2-服务端心跳检测超时，关闭连接(需要重连)  3-Token验证失败，关闭连接(无需重连)
            future.channel().closeFuture().sync();
            System.out.println("TCP连接已关闭");
        } finally {
            group.shutdownGracefully();
        }

    }

}
