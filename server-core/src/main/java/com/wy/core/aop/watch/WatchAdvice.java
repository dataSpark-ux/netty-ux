package com.wy.core.aop.watch;

import com.wy.core.aop.Aop;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 调用阈值监控, 线上关闭
 *
 * @author wangyi
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "app", value = "watch", havingValue = "true")
public class WatchAdvice extends Aop {

    @Around("@annotation(com.wy.core.aop.watch.Watch)")
    public Object invoke(ProceedingJoinPoint point) throws Throwable {
        Watch watch = getAnnotation(point, Watch.class);
        long start = System.currentTimeMillis();
        Object object;
        try {
            object = point.proceed();
        } finally {
            long offset = watch.limitUnit()
                               .convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            if (offset > watch.limit()) {
                log.warn(String.format("[ %s ][ %s ]超过阀值[ %s ], 最大[ %s ], 单位[ %s ]",
                        point.getTarget()
                             .getClass(),
                        watch.value(),
                        offset,
                        watch.limit(),
                        watch.limitUnit()
                ));
            }
        }
        return object;
    }


}
