package com.wy.core.queue.hash;

/**
 * 固定槽
 * @author wangyi
 */
public interface MsgHashSlot {

    /**
     * 下发到终端
     */
    String DOWN = "down";

    /**
     * 下发到报警监控
     */
    String ALARM = "alarm";
}
