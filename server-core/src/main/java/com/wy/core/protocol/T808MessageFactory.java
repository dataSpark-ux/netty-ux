package com.wy.core.protocol;


import com.wy.common.util.Tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 808消息体,工厂工具类, 通过消息ID生成消息实体
 *
 * @Author 2018-12-12
 */
public final class T808MessageFactory {

    private static Map<Integer, Class<? extends IPackageBody>> classMap = new ConcurrentHashMap<>();

    public static IPackageBody Create(int messageType, byte[] messageBodyBytes) {
        Class<? extends IPackageBody> aClass = classMap.computeIfAbsent(messageType, T808MessageFactory::build);
        try {
            if (aClass == null) {
                return null;
            }
            IPackageBody iPackageBody = aClass.newInstance();
            iPackageBody.readFromBytes(messageBodyBytes);
            return iPackageBody;
        } catch (InstantiationException e) {
//            e.printStackTrace();
        } catch (IllegalAccessException e) {
//            e.printStackTrace();
        }
        return null;
    }

    private static Class<? extends IPackageBody> build(Integer integer) {
        String nameSpace = T808MessageFactory.class.getPackage()
                                                   .getName();
        String className = nameSpace + ".JT_" + Tools.ToHexString(integer, 2)
                                                     .toUpperCase();
        try {
            return (Class<? extends IPackageBody>) Class.forName(className);
        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
            return null;
        }
    }
}