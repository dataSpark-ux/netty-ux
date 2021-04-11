package com.netty.common.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wy
 * @Description 作为 MessageHandler 的容器。
 * @createTime 2021/03/14
 */
@Slf4j
@Component
public class MessageHandlerContainer implements InitializingBean {

    private final Map<String, MessageHandler> handlers = new HashMap<>();

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 实现 InitializingBean 接口，在 #afterPropertiesSet() 方法中，
     * 扫描所有 MessageHandler Bean ，添加到 MessageHandler 集合中。
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 通过 ApplicationContext 获得所有 MessageHandler Bean
        // 获得所有 MessageHandler Bean
        applicationContext.getBeansOfType(MessageHandler.class).values()
                // 添加到 handlers 中
                .forEach(messageHandler -> handlers.put(messageHandler.getType(), messageHandler));
        log.info("[afterPropertiesSet][消息处理器数量：{}]", handlers.size());
    }

    /**
     * 获得类型对应的 MessageHandler
     * 在 #getMessageHandler(String type) 方法中，获得类型对应的 MessageHandler 对象。
     * 会在 MessageDispatcher 调用该方法。
     *
     * @param type 类型
     * @return MessageHandler
     */
    MessageHandler getMessageHandler(String type) {
        MessageHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException(String.format("类型(%s) 找不到匹配的 MessageHandler 处理器", type));
        }
        return handler;
    }

    /**
     * 在 #getMessageClass(MessageHandler handler) 方法中，
     * 通过 MessageHandler 中，通过解析其类上的泛型，
     * 获得消息类型对应的 Class 类。这是参考 rocketmq-spring
     * 项目的 DefaultRocketMQListenerContainer#getMessageType() 方法，进行略微修改。
     */
    static Class<? extends Message> getMessageClass(MessageHandler handler) {
        // 获取Bean 对应的class类名，因为有可能被aop代理过
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(handler);
        // 获得接口的Type 数组
        Type[] interfaces = targetClass.getGenericInterfaces();
        Class<?> superclass = targetClass.getSuperclass();
        // 此处，是以父类的接口为准
        while ((Objects.isNull(interfaces)
                || 0 == interfaces.length)
                && Objects.nonNull(superclass)) {
            interfaces = superclass.getGenericInterfaces();
            superclass = targetClass.getSuperclass();
        }
        if (Objects.nonNull(interfaces)) {
            // 遍历 interfaces 数组
            for (Type type : interfaces) {
                // 要求 type 是泛型参数
                if (type instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    // 要求是 MessageHandler 接口
                    if (Objects.equals(parameterizedType.getRawType(), MessageHandler.class)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        // 取首个元素
                        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
                            return (Class<Message>) actualTypeArguments[0];
                        } else {
                            throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
                        }
                    }
                }
            }
        }
        throw new IllegalStateException(String.format("类型(%s) 获得不到消息类型", handler));
    }
}
