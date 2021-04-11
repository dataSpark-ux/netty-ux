package com.netty.common.message;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wy
 * @Description 通信协议的消息体。
 * @createTime 2021/03/14
 */
@Data
@NoArgsConstructor
public class Invocation {

    /**
     * 类型
     */
    private String type;
    /**
     * 消息，JSON 格式
     */
    private String message;

    public Invocation(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public Invocation(String type, Message message) {
        this.type = type;
        this.message = JSON.toJSONString(message);
    }

}
