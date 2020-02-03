package com.wy.core.queue.hash;

import com.wy.core.queue.IMsgHash;

import java.io.Serializable;

/**
 * @author wangyi
 */
public class MsgHash implements IMsgHash {

    private int slot;

    public MsgHash(int slot) {
        this.slot = slot;
    }

    /**
     * @param serializable
     * @return
     */
    public Serializable hash(Serializable serializable) {
        return Math.abs(serializable.hashCode()) % slot;
    }
}
