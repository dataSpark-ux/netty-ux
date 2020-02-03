package com.wy.core.protocol;

/**
 * 808消息抽象接口
 *
 * @Author 2018-12-12
 */
public interface IPackageBody {

    /**
     * 数据转成字节流
     * @return
     */
    byte[] writeToBytes();

    /**
     * 读取字节流，解析出数据
     * @param messageBodyBytes
     */
    void readFromBytes(byte[] messageBodyBytes);

}