package com.netty.common.message;

import io.netty.channel.Channel;

/**
 * @author wy
 * @Description 消息处理器接口
 * @createTime 2021/03/14
 */
public interface MessageHandler<T extends Message> {

    /**
     * 执行处理消息
     *
     * @param channel 通道
     * @param message 消息
     */
    void execute(Channel channel, T message);

    /**
     *  消息类型
     * @return ，即每个 Message 实现类上的 TYPE 静态字段
     */
    String getType();
}
