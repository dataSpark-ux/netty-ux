package com.wy.core.cache;

import com.google.common.cache.RemovalListener;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 用于从内存读, 双写到内存和redis, 自带过期时间
 * @author wy
 */
@Slf4j
public class MemoryCache<K, V> {

    private RedisTemplate<K, V> redisTemplate;

    public MemoryCache(int expireTime, TimeUnit expireTimeUnit, RedisTemplate<K, V> redisTemplate) {
        this(expireTime, expireTimeUnit, null, redisTemplate);
    }

    public MemoryCache(int expireTime, TimeUnit expireTimeUnit, RemovalListener<K, V> removalListener, RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(K k, V v) {
        redisTemplate.boundValueOps(k)
                     .set(v);
    }

    public V get(K k) {
        V v = null;
        try {
            v = redisTemplate.boundValueOps(k)
                    .get();
        } catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    public void delete(K k) {
        redisTemplate.delete(k);
    }

}
