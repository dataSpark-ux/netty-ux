package com.netty.client.controller;

import com.netty.client.client.NettyClient;
import com.netty.common.message.Invocation;
import com.netty.common.message.auth.AuthRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wy
 * @Description
 * @createTime 2021/03/17
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @Resource
    private NettyClient nettyClient;


    @GetMapping("/mock")
    public String mock(String type,String message) {
        AuthRequest build = AuthRequest
                .builder().accessToken(message).build();
        Invocation invocation = new Invocation(type, build);
        nettyClient.send(invocation);
        return "success";
    }
}
