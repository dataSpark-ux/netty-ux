package com.netty.app.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wy
 * @Description 1  客户端 Channel 的管理。代码如下：
 * @createTime 2021/03/14
 */
@Component
@Slf4j
public class NettyChannelManager {
    /**
     * {@link Channel#attr(AttributeKey)} 属性中，表示 Channel 对应的用户
     */
    private static final AttributeKey<String> CHANNEL_ATTR_KEY_USER = AttributeKey.newInstance("user");

    /**
     * channel映射
     */
    private ConcurrentHashMap<ChannelId, Channel> channels = new ConcurrentHashMap<>();
    /**
     * 用户与 Channel 的映射。
     * <p>
     * 通过它，可以获取用户对应的 Channel。这样，我们可以向指定用户发送消息。
     */
    private ConcurrentHashMap<String, Channel> userChannels = new ConcurrentHashMap<>();

    /**
     * 添加 Channel 到 {@link #channels} 中
     *
     * @param channel Channel
     */
    public void add(Channel channel) {
        channels.put(channel.id(), channel);
        log.info("[add][一个连接({})加入]", channel.id());
    }

    /**
     * 添加指定用户到 {@link #userChannels} 中
     *
     * @param channel Channel
     * @param user    用户
     */
    public void addUser(Channel channel, String user) {
        Channel existChannel = channels.get(channel.id());
        if (existChannel == null) {
            log.error("[addUser][连接({}) 不存在]", channel.id());
            return;
        }
        // 设置属性
        channel.attr(CHANNEL_ATTR_KEY_USER).set(user);
        // 添加到 userChannels
        userChannels.put(user, channel);
    }

    /**
     * 将 Channel 从 {@link #channels} 和 {@link #userChannels} 中移除
     *
     * @param channel Channel
     */
    public void remove(Channel channel) {
        // 移除 channels
        channels.remove(channel.id());
        if (channel.hasAttr(CHANNEL_ATTR_KEY_USER)) {
            userChannels.remove(channel.attr(CHANNEL_ATTR_KEY_USER).get());
        }
        log.info("[remove][一个连接({})离开]", channel.id());
    }
}
