package com.wy.service.service;

import com.wy.core.queue.IMsg;

import java.util.Date;

/**
 * @param <M> 过滤的消息类型
 * @author yuan
 */
public interface FilterService<M extends IMsg> {

    /**
     * 过滤消息
     *
     * @param msg
     */
    void filter(M msg);

    /**
     * 过滤消息
     *
     * @param key 上线号
     */
    void filter(String key);

    /**
     * 清除过滤限制
     *
     * @param key
     */
    void clear(String key);

    /**
     * 查询
     *
     * @param key
     * @return
     */
    Date get(String key);

}
