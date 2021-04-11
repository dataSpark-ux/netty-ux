package com.netty.client.client.handler;

import com.netty.client.client.NettyClient;
import com.netty.common.message.Invocation;
import com.netty.common.message.heartbeat.HeartbeatRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description 实现客户端 Channel 断开连接、异常时的处理。
 * 1  在类上添加 @ChannelHandler.Sharable 注解，标记这个 ChannelHandler 可以被多个 Channel 使用
 * @createTime 2021/03/16
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private NettyClient nettyClient;

    /**
     * 实现在和服务端断开连接时，
     * 调用 NettyClient 的 #reconnect() 方法，实现客户端定时和服务端重连。
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 发起重连
        nettyClient.reconnect();
        // 连续触发事件
        super.channelInactive(ctx);
    }

    /**
     * 在处理 Channel 的事件发生异常时，调用 Channel 的 #close() 方法，断开和客户端的连接。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[exceptionCaught][连接({}) 发生异常]", ctx.channel().id(), cause);
        // 断开连接
        ctx.channel().close();
    }

    /**
     * 法，在客户端在空闲时，向服务端发送一次心跳，即心跳机制
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            log.info("[userEventTriggered][发起一次心跳]");
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            ctx.writeAndFlush(new Invocation(HeartbeatRequest.TYPE, heartbeatRequest))
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }

    }
}
