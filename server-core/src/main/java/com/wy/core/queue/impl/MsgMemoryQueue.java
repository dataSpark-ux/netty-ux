package com.wy.core.queue.impl;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.wy.common.util.SpringHelper;
import com.wy.core.queue.IMsgQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 内存队列
 * @author wangyi
 */
@Slf4j
public class MsgMemoryQueue<T> implements IMsgQueue<T>, Gauge<Long> {

    private final BlockingQueue<T> queue;

    private final String name;
    private final Counter metricCount;
    private final Meter metricConsume;


    public MsgMemoryQueue(String name, int size) {
        this.name = name;
        this.queue = new LinkedBlockingDeque<>(size);
        MetricRegistry metricRegistry = SpringHelper.getBean(MetricRegistry.class);
        this.metricCount = metricRegistry.counter(name + "提交");
        this.metricConsume = metricRegistry.meter(name + "消费");
        metricRegistry.register(name + "剩余", this);
    }

    public String getName() {
        return this.name;
    }

    /**
     * 放入队列时候, 设置指标
     *
     * @param message
     */
    @Override
    public void put(T message) {
        try {
            this.queue.put(message);
        } catch (InterruptedException e) {
            log.error(this.name + " => 线程中断, 无法插入新数据");
        }
        this.metricCount.inc();
    }

    @Override
    public List<T> pull() {
        try {
            this.metricConsume.mark();
            return Collections.singletonList(this.queue.take());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            return null;
        }
    }


    @Override
    public Long getValue() {
        return (long) this.queue.size();
    }
}
