package com.netty.client.client.handler;

import com.netty.common.message.MessageHandler;
import com.netty.common.message.auth.AuthResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wy
 * @Description 客户端处理服务端的认证响应
 * @createTime 2021/03/17
 */
@Component
@Slf4j
public class AuthResponseHandler implements MessageHandler<AuthResponse> {

    @Override
    public void execute(Channel channel, AuthResponse message) {
        log.info("[execute][认证结果：{}]", message);
    }

    @Override
    public String getType() {
        return AuthResponse.TYPE;
    }
}
