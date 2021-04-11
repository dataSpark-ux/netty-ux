package com.netty.client.client;

import com.netty.client.client.handler.NettyClientHandlerInitializer;
import com.netty.common.message.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author wy
 * @Description
 * @createTime 2021/03/16
 */
@Component
@Slf4j
public class NettyClient {
    /**
     * 重连频率
     */
    private static final Integer RECONNECT_SECONDS = 20;

    @Value("${netty.server.host:127.0.0.1}")
    private String serverHost;

    @Value("${netty.server.port:9099}")
    private Integer serverPort;
    /**
     * 用于客户端对服务端的链接，数据读写
     */
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    @Resource
    private NettyClientHandlerInitializer nettyClientHandlerInitializer;
    /**
     * netty client channel
     */
    private volatile Channel channel;

    /**
     * 启动
     */
    @PostConstruct
    public void start() {
        // 创建bootstrap 对象，用于nettyClient 启动
        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .group(eventLoopGroup)
                // 指定 Channel 为客户端 NioSocketChannel
                .channel(NioSocketChannel.class)
                // 指定连接服务器的地址
                .remoteAddress(serverHost, serverPort)
                // TCP Keepalive 机制，实现 TCP 层级的心跳保活功能
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 允许较小的数据包的发送，降低延迟
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(nettyClientHandlerInitializer);

        // 连接服务器，并异步等待成功，即启动客户端。
        //同时，添加回调监听器 ChannelFutureListener，
        // 在连接服务端失败的时候，调用 #reconnect() 方法，
        //实现定时重连。😈 具体 #reconnect()
        bootstrap.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("[start][Netty Client 连接服务器({}:{}) 失败]", serverHost, serverPort);
                    reconnect();
                    return;
                }
                channel = future.channel();
                log.info("[start][Netty Client 连接服务器({}:{}) 成功]", serverHost, serverPort);
            }
        });
    }

    /**
     * 通过调用 EventLoop 提供的 #schedule(Runnable command, long delay,
     * TimeUnit unit) 方法，实现定时逻辑。而在内部的具体逻辑，
     * 调用 NettyClient 的 #start() 方法，发起连接 Netty 服务端
     * 又因为 NettyClient 在 #start() 方法在连接 Netty 服务端失败时，
     * 又会调用 #reconnect() 方法，从而再次发起定时重连。如此循环反复，
     * 知道 Netty 客户端连接上 Netty 服务端。
     */
    public void reconnect() {
        eventLoopGroup.schedule(() -> {
            log.info("reconnect 开始重连");
            try {
                this.start();
            } catch (Exception e) {
                log.error("重连失败");
            }

        }, RECONNECT_SECONDS, TimeUnit.SECONDS);
        log.info("[reconnect][{} 秒后将发起重连]", RECONNECT_SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.close();
        }
        // 关闭
        eventLoopGroup.shutdownGracefully();
    }

    public void send(Invocation invocation) {
        if (channel == null) {
            log.error("连接不存在");
            return;
        }
        if (!channel.isActive()) {
            log.error("[send][连接({})未激活]", channel.id());
            return;
        }
        // 发生消息
        channel.writeAndFlush(invocation);
    }
}
