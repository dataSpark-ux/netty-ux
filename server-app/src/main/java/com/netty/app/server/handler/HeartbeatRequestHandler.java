package com.netty.app.server.handler;

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
public class HeartbeatRequestHandler implements MessageHandler<HeartbeatRequest> {
    @Override
    public void execute(Channel channel, HeartbeatRequest message) {
        log.info("[execute][收到连接({}) 的心跳请求]", channel.id());
        // 响应心跳
        HeartbeatResponse response = new HeartbeatResponse();
        channel.writeAndFlush(new Invocation(HeartbeatResponse.TYPE, response));
    }

    @Override
    public String getType() {
        return HeartbeatRequest.TYPE;
    }
}
