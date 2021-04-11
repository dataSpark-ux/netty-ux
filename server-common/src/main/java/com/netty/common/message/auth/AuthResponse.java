package com.netty.common.message.auth;

import com.netty.common.message.Message;
import lombok.Builder;
import lombok.Data;

/**
 * @author wy
 * @Description 认证响应
 * @createTime 2021/03/17
 */
@Data
@Builder
public class AuthResponse implements Message {

    /**
     * 用户认证响应
     */
    public static final String TYPE = "AUTH_RESPONSE";
    /**
    *
    */
    private Integer code;

    /**
     * 响应提示
     */
    private String message;
}
