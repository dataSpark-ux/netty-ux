package com.wy.core.aop.watch;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wy
 * @date 2019/2/18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD
})
@Inherited
@Documented
public @interface Watch {

    /**
     * 描述
     *
     * @return
     */
    String value();

    /**
     * 超过这个警戒线告警
     *
     * @return
     */
    long limit() default 1L;

    /**
     * 警戒线单位
     *
     * @return
     */
    TimeUnit limitUnit() default TimeUnit.SECONDS;
}
