package com.netty.app.server.handler;

import cn.hutool.core.util.StrUtil;
import com.netty.common.message.Invocation;
import com.netty.common.message.MessageHandler;
import com.netty.common.message.auth.AuthRequest;
import com.netty.common.message.auth.AuthResponse;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description 服务端处理客户端的认证请求
 * @createTime 2021/03/17
 */
@Component
public class AuthRequestHandler implements MessageHandler<AuthRequest> {

    @Resource
    private NettyChannelManager nettyChannelManager;

    @Override
    public void execute(Channel channel, AuthRequest message) {
        if (StrUtil.isEmpty(message.getAccessToken())) {
            AuthResponse response = AuthResponse.builder().code(1)
                    .message("认证accessToken 未传入").build();
            channel.writeAndFlush(new Invocation(AuthResponse.TYPE, response));
            return;
        }
        // TODO
        // 将用户和channel绑定
        // 考虑到代码简化，直接使用accessToken为user
        nettyChannelManager.addUser(channel, message.getAccessToken());
        AuthResponse response = AuthResponse.builder().code(0).message("SUCCESS").build();
        channel.writeAndFlush(new Invocation(AuthResponse.TYPE, response));

    }

    @Override
    public String getType() {
        return AuthRequest.TYPE;
    }
}
