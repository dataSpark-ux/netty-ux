package com.netty.app.server.handler;

import com.netty.common.codec.InvocationDecoder;
import com.netty.common.codec.InvocationEncoder;
import com.netty.common.message.MessageDispatcher;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wy
 * @Description
 * @createTime 2021/03/14
 */
@Component
public class NettyServerHandlerInitializer extends ChannelInitializer<Channel> {
    /**
     * 心跳超时时间
     */
    public static final Integer READ_TIMEOUT_SECONDS = 3 * 60;

    @Resource
    private MessageDispatcher messageDispatcher;

    @Resource
    private NettyServerHandler nettyServerHandler;

    /**
     * 在每一个客户端与服务端建立完成连接时，
     * 服务端会创建一个 Channel 与之对应。此时，
     * NettyServerHandlerInitializer 会进行执行 #initChannel(Channel c) 方法，进行自定义的初始化。
     * 在 #initChannel(Channel ch) 方法的 ch 参数，就是此时创建的客户端 Channel。
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 获得channel对应的ChannelPipeline调用 Channel 的 #pipeline() 方法，
        //获得客户端 Channel 对应的 ChannelPipeline。ChannelPipeline 由一系列的 ChannelHandler 组成
        //，又或者说是 ChannelHandler 链。
        //这样， Channel 所有上所有的事件都会经过 ChannelPipeline，被其上的 ChannelHandler 所处理
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                // 空闲检测，超过指定时间，主动断开连接¬
                .addLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                // 编码
                .addLast(new InvocationEncoder())
                // 解码
                .addLast(new InvocationDecoder())
                // 消息分发
                .addLast(messageDispatcher)
                // 服务端处理器
                .addLast(nettyServerHandler);

    }
}
