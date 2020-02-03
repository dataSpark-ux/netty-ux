package com.wy.core.queue;

import com.wy.core.config.ApplicationProperty;
import com.wy.core.queue.hash.MsgHash;
import com.wy.core.queue.impl.MsgMemoryQueue;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多队列, 保证同一个车辆的数据在同一个队列被同一个线程消费
 *
 */
@Slf4j
@Component
@DependsOn("SpringHelper")
public class MsgQueueFactory implements InitializingBean {

    @Autowired
    private ApplicationProperty applicationProperty;

    /**
     * 按组分不同的队列
     */
    private Map<Serializable, IMsgQueue> groupQueueList = new ConcurrentHashMap<>();

    /**
     * 计算到那个队列
     */
    private IMsgHash msgHash;

    /**
     * hash到槽
     *
     * @param message
     * @param
     * @return
     */
    public IMsgQueue send(IMsg message) {
        return sendSpecified(message, msgHash.hash(message.getHash()));
    }


    /**
     * 发往指定槽
     *
     * @param message
     * @param specified
     * @return
     */
    public <T> IMsgQueue<T> sendSpecified(T message, Serializable specified) {
        IMsgQueue<T> build = groupQueueList.get(specified);
        if (build == null) {
            log.warn(String.format("没有消息队列 => [%s]", specified));
            return null;
        }
        build.put(message);
        return build;
    }

    /**
     * @param hash
     * @return
     */
    public <T> IMsgQueue<T> build(Serializable hash) {
        IMsgQueue msgQueue = groupQueueList.computeIfAbsent(hash, k -> new MsgMemoryQueue(String.format("消息队列[%s]", k), applicationProperty.getMessageQueueSize()));
        if (log.isDebugEnabled()) {
            log.debug(String.format("创建消息队列 ==> [ %s ]", hash));
        }
        return msgQueue;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        msgHash = new MsgHash(applicationProperty.getMessageQueueCount());
    }
}
