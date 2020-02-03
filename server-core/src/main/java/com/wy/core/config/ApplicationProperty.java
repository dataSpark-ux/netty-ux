package com.wy.core.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局配置
 *
 * @author
 */
@Data
@ConfigurationProperties(prefix = "app")
public class ApplicationProperty implements InitializingBean {

    /**
     * 负载编号
     */
    private String system;
    /**
     * 开发环境
     */
    private boolean dev;
    /**
     *
     */
    private boolean watch;
    /**
     * 鉴权码
     */
    private String authCode;
    /**
     * 队列个数
     */
    private int messageQueueCount;
    /**
     * 每个队列的最大元素个数
     */
    private int messageQueueSize;
    /**
     * 初始化线程池线程数量
     */
    private int asyncThreads = 10;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
