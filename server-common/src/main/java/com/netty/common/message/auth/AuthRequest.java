package com.netty.common.message.auth;

import com.netty.common.message.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wy
 * @Description
 * @createTime 2021/03/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest implements Message {

    public static final String TYPE = "AUTH_REQUEST";

    /**
     * 认证accessToken
     */
    private String accessToken;


}
