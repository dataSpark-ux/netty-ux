package com.wy.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 *
 * @author wangyi
 */
@Data
@ConfigurationProperties(prefix = "collect")
@Configuration
public class CollectProperty {

    private boolean enable = true;

    /**
     * 服务端口
     */
    private int listenPort = 0;

    /**
     * 消费线程大小
     */
    private int consumeThreadCount;

    /**
     * 多久没有上报消息认为超时(单位秒)
     */
    private int idleTimeout;

}
