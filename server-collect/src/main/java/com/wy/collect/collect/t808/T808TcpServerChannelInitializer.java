package com.wy.collect.collect.t808;

import com.wy.collect.collect.t808.decoder.T808ProtocolDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author wangyi
 */
@Slf4j
@Component
public class T808TcpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    T808ChannelHandler t808ChannelHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 超过20分钟未收到客户端消息则自动断开客户端连接
        ch.pipeline().addLast("idleStateHandler",
                new IdleStateHandler(20, 0, 0, TimeUnit.MINUTES));
        // 消息编解码
        ch.pipeline().addLast("decoder", new T808ProtocolDecoder());
        ch.pipeline().addLast("encoder", new ByteArrayEncoder());
        // 解析成T808Message消息 - 消息业务派发
        ch.pipeline().addLast(t808ChannelHandler);
    }
}
