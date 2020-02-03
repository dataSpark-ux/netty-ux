package com.wy.core.config;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperty.class)
public class ApplicationConfiguration {

    @Autowired
    private ApplicationProperty applicationProperty;


    @Bean
    ExecutorService getAsyncExecutor() {
        return new ThreadPoolExecutor(
                applicationProperty.getAsyncThreads(),
                applicationProperty.getAsyncThreads(),
                0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(
                        applicationProperty.getAsyncThreads() * Runtime.getRuntime().availableProcessors())
        );
    }

    @Bean
    MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }

}
