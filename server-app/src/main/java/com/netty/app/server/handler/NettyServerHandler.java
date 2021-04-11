package com.netty.app.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description  该类实现客户端 Channel 建立连接、断开连接、异常时的处理。
 * @createTime 2021/03/14
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private NettyChannelManager channelManager;



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelManager.add(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        channelManager.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[exceptionCaught][连接({}) 发生异常]", ctx.channel().id(), cause);
        ctx.channel().close();
    }
}
