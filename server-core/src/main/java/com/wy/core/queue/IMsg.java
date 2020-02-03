package com.wy.core.queue;

/**
 * @author wangyi
 */
public interface IMsg<T> {
    /**
     * 获取hash
     *
     * @return
     */
    String getHash();

    /**
     * 获取消息类型
     *
     * @return
     */
    T getMessageType();
}
