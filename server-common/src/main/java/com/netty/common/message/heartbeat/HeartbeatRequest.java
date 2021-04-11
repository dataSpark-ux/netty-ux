package com.netty.common.message.heartbeat;

import com.netty.common.message.Message;
import lombok.Data;

/**
 * @author wy
 * @Description 心跳请求
 * @createTime 2021/03/16
 */
@Data
public class HeartbeatRequest implements Message {

    /**
     * 类型 - 心跳请求
     */
    public static final String TYPE = "HEARTBEAT_REQUEST";

    @Override
    public String toString() {
        return "{}";
    }
}
