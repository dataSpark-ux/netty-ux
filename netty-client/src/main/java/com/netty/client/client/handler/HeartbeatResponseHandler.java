package com.netty.client.client.handler;

import com.netty.common.message.Invocation;
import com.netty.common.message.MessageHandler;
import com.netty.common.message.heartbeat.HeartbeatRequest;
import com.netty.common.message.heartbeat.HeartbeatResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wy
 * @Description 处理心跳
 * @createTime 2021/03/17
 */
@Component
@Slf4j
public class HeartbeatResponseHandler implements MessageHandler<HeartbeatResponse> {

    @Override
    public void execute(Channel channel, HeartbeatResponse message) {
        log.info("[execute][收到连接({}) 的心跳响应]", channel.id());
    }

    @Override
    public String getType() {
        return HeartbeatResponse.TYPE;
    }
}
