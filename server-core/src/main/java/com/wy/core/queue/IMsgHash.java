package com.wy.core.queue;

import java.io.Serializable;

/**
 * @author wy
 */
public interface IMsgHash {

    Serializable hash(Serializable serializable);
}
