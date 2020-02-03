package com.wy.core.protocol;

import com.wy.common.util.BufferUtil;
import com.wy.common.util.Tools;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 808消息头
 *
 * @Author 2018-12-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class T808MessageHeader {

    /**
     * 消息ID，2个字节，无符号16位
     */
    private int messageType;
    /**
     * 消息体属性
     */
    private short messageBodyProperty;
    /**
     * 上线号
     */
    private String onlineNo;
    /**
     * 消息流水号
     */
    private Short messageSerialNo;
    /**
     * 总包数
     */
    private short messageTotalPacketsCount;
    /**
     * 分包号
     */
    private short messagePacketNo;

    public final int getHeaderSize() {
        // 有分包
        if (getIsPackage()) {
            // 两个分隔符，一个校验字节
            return 16 + 3;
        } else {
            // 两个分隔符，一个校验字节
            return 12 + 3;
        }
    }

    /**
     * 消息体长度
     */
    public final int getMessageSize() {
        return getMessageBodyProperty() & 0x03FF;
    }

    public final void setMessageSize(int value) {
        boolean res = getIsPackage();
        if (res) {
            setMessageBodyProperty((short) (0x2000 | value));
        } else {
            setMessageBodyProperty((short) (value));
        }
    }

    /**
     * 判断是否加密
     *
     * @return true 消息体经过RSA加密
     */
    public final boolean isEncryption() {
        // 是否 RSA 算法加密
        return (getMessageBodyProperty() & 0x0400) != 0;
    }

    /**
     * 分包发送
     */
    public final boolean getIsPackage() {
        return (getMessageBodyProperty() & 0x2000) == 0x2000;
    }

    public final T808MessageHeader setIsPackage(boolean value) {
        if (value) {
            messageBodyProperty |= 0x2000;
        } else {
            messageBodyProperty &= 0xDFFF;
        }
        return this;
    }

    public final byte[] writeToBytes() {
        BufferUtil buff = new BufferUtil();
        buff.putShort((short) getMessageType());
        buff.putShort(getMessageBodyProperty());
        byte[] onlineNoBytes = Tools.HexString2Bytes(onlineNo);
        buff.put(onlineNoBytes);
        buff.putShort(getMessageSerialNo());
        if (getIsPackage()) {
            buff.putShort(getMessageTotalPacketsCount());
            buff.putShort(getMessagePacketNo());
        }
        return buff.array();
    }

    public final void readFromBytes(byte[] headerBytes) {
        BufferUtil buff = new BufferUtil(headerBytes);
        setMessageType(buff.getShort() & 0xffff);
        setMessageBodyProperty(buff.getShort());
        byte[] onlineNoBytes = buff.gets(6);
        setOnlineNo(String.format("%02X", onlineNoBytes[0])
                + String.format("%02X", onlineNoBytes[1])
                + String.format("%02X", onlineNoBytes[2])
                + String.format("%02X", onlineNoBytes[3])
                + String.format("%02X", onlineNoBytes[4])
                + String.format("%02X", onlineNoBytes[5]));
        setMessageSerialNo(buff.getShort());
        if (getIsPackage()) {
            setMessageTotalPacketsCount(buff.getShort());
            setMessagePacketNo(buff.getShort());
        }
    }

}