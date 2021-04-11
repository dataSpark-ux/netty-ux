package com.netty.common.message.heartbeat;

import com.netty.common.message.Message;
import lombok.Data;

/**
 * @author wy
 * @Description 心跳响应
 * @createTime 2021/03/16
 */
@Data
public class HeartbeatResponse implements Message {
    /**
     * 类型 - 心跳响应
     */
    public static final String TYPE = "HEARTBEAT_RESPONSE";


    @Override
    public String toString() {
        return "{}";
    }
}
