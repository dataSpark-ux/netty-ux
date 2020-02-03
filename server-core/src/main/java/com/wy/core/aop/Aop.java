package com.wy.core.aop;

import javassist.NotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class Aop {

    /**
     * @param point
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getAnnotation(JoinPoint point, Class clazz) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getMethod(signature.getName(), signature.getParameterTypes());
        return (T) method.getAnnotation(clazz);
    }

    /**
     * @param point
     * @return
     */
    protected String getMethodName(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        return method.getName();
    }

    protected Map<String, Object> getArgs(JoinPoint point) throws NotFoundException {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Map<String, Object> map = new HashMap<>();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = u.getParameterNames(signature.getMethod());
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], point.getArgs()[i]);//paramNames即参数名
        }
        return map;
    }
}
