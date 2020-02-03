package com.wy.core.protocol;

import com.wy.core.queue.IMsg;

import java.io.Serializable;

/**
 *
 * @author wangyi
 */
public interface IPackage extends IMsg<Integer>, Serializable {

    /**
     * 获取会话
     * @return
     */
    String getSession();

    /**
     * 获取字节
     * @return
     */
    byte[] getBytes();

}
