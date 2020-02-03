package com.wy.core.aop.lock;

import com.wy.common.util.SpelHelper;
import com.wy.core.aop.Aop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 *
 * @author wangyi
 */
@Component
@Aspect
public class LockAdvice extends Aop {

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.wy.core.aop.lock.Lock)")
    Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Lock annotation = getAnnotation(joinPoint, Lock.class);
        Map<String, Object> args = getArgs(joinPoint);
        Object redisKey = SpelHelper.exec(annotation.key(), args);
        if (Objects.isNull(redisKey)) {
            return joinPoint.proceed();
        }
        RLock lock = redissonClient.getLock(annotation.prefix() + redisKey.toString());
        try {
            lock.lock();
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }
}
