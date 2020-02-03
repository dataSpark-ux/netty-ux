package com.wy.core.trace;


import com.alibaba.fastjson.JSON;
import com.wy.core.protocol.IPackage;
import lombok.Data;

/**
 * @author wangyi
 */
@Data
public class TraceMessage {

    /**
     * 类型
     */
    private int type;

    /**
     * 卡号
     */
    private String no;

    /**
     * 内容
     */
    private String content;

    /**
     * 发送事件
     */
    private long time;

    /**
     * 原始报文
     */
    private String origin;

    /**
     * 上报 | 下发
     */
    private boolean up;

    /**
     * 下发是否发送成功
     */
    private boolean downStatus;

    public TraceMessage(IPackage pkg) {
        this.type = pkg.getMessageType();
        this.no = pkg.getHash();
        this.time = System.currentTimeMillis();
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
