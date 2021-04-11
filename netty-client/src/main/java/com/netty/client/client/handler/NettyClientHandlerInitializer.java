package com.netty.client.client.handler;

import com.netty.common.codec.InvocationDecoder;
import com.netty.common.codec.InvocationEncoder;
import com.netty.common.message.MessageDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description 实现和服务端建立连接后，添加相应的 ChannelHandler 处理器。
 * @createTime 2021/03/16
 */
@Component
public class NettyClientHandlerInitializer extends ChannelInitializer<Channel> {


    /**
     * Context Switch
     * 心跳超时时间
     */
    private static final Integer READ_TIMEOUT_SECONDS = 60;

    /**
     *
     */
    @Resource
    private MessageDispatcher messageDispatcher;


    @Resource
    private NettyClientHandler nettyClientHandler;


    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                // 心跳检测
                .addLast(new IdleStateHandler(READ_TIMEOUT_SECONDS, 0, 0))
                // 空闲检测 超过指定时间，主动断开连接
                .addLast(new ReadTimeoutHandler(3 * READ_TIMEOUT_SECONDS))
                // 解码器
                .addLast(new InvocationDecoder())
                // 编码器
                .addLast(new InvocationEncoder())
                // 消息分发
                .addLast(messageDispatcher)
                // 客户端处理器
                .addLast(nettyClientHandler);
    }
}
