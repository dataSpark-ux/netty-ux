package com.wy.collect.t808;

import com.sinoxx.sserver.core.exception.SinoXXException;
import com.sinoxx.sserver.core.protocol.IPackage;
import com.sinoxx.sserver.core.protocol.IPackageBody;
import com.sinoxx.sserver.core.util.MyBuffer;
import com.sinoxx.sserver.core.util.ToolBuff;
import com.wy.core.protocol.IPackage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * 808消息
 *
 * @Author 2018-12-12
 */
@Slf4j
@Data
@AllArgsConstructor
@Builder
public class T808Message implements IPackage, Serializable {


    private static byte _PrefixID = 0x7E;

    /**
     * 消息报文头
     */
    private T808MessageHeader header = new T808MessageHeader();

    private int status;
    /**
     * 如果是分包，分包是不能解析的，等待和其他包合成一个完整的包才能解析
     */
    private byte[] childPacket;

    private byte[] _checkSum = null;

    private IPackageBody messageContents;

    private String plateNo;

    private String errorMessage;

    /**
     * 保留原始数据
     */
    private String hexMsg;

    public T808Message() {
    }

    public T808Message(String onlineNo, int messageType, IPackageBody echoData) {
        this.setMessageContents(echoData);
        this.setHeader(new T808MessageHeader());
        this.getHeader().setMessageType(messageType);
        this.getHeader().setOnlineNo(onlineNo);
        this.getHeader().setIsPackage(false);
    }

    public T808Message(T808MessageHeader header, IPackageBody messageContents) {
        this.header = header;
        this.messageContents = messageContents;
    }

    public String getOnlineNo() {
        if (header != null) {
            return header.getOnlineNo();
        }
        return "";
    }

    @Override
    public String toString() {
        if (messageContents != null) {
            return messageContents.toString();
        } else if (this.getHeader().getIsPackage()) {
            String str = "分包号:" + this.getHeader().getMessagePacketNo() + ",总包数:" + this.getHeader().getMessageTotalPacketsCount();
            if (childPacket != null && childPacket.length > 0) {
                str += ", 分包长度:" + childPacket.length;
            }
            return str;
        }
        return "";
    }

    public final byte[] writeToBytes() {
        MyBuffer buff = new MyBuffer();
        // buff. mark();
        byte[] bodyBytes = null;
        if (getMessageContents() != null) {
            bodyBytes = getMessageContents().writeToBytes();
        }
        // ArrayList<Byte> messageBytes = new ArrayList<Byte>();
        if (bodyBytes != null) {
            header.setMessageSize(bodyBytes.length);
            header.setIsPackage(false);
            byte[] headerBytes = header.writeToBytes();
            buff.put(headerBytes);
            buff.put(bodyBytes);
        } else {
            header.setMessageSize(0);
            byte[] headerBytes = header.writeToBytes();
            buff.put(headerBytes);
        }
        // int pos = buff. position();
        // byte[] messageBytes = new byte[pos - buff. markValue() + 1];
        // buff. get(messageBytes);
        byte[] messageBytes = buff.array();
        byte checkSum = getCheckXor(messageBytes, 0, messageBytes.length);
        // 填充校验码
        // messageBytes[messageBytes.length - 1] = checkSum;
        buff.put(checkSum);
        // 转义
        byte[] escapedBytes = escape(buff.array());
        buff.clear();
        buff.put(_PrefixID);
        buff.put(escapedBytes);
        buff.put(_PrefixID);
        byte[] data = buff.array();
        return data;
    }

    public final void readFromBytes(byte[] messageBytes) {
        hexMsg = ToolBuff.encodeHexString(messageBytes);

        // 01 数据转义还原
        byte[] validMessageBytes = unEscape(messageBytes);

        // 02 检测校验码
        byte xor = getCheckXor(validMessageBytes, 1, validMessageBytes.length - 2);
        byte realXor = validMessageBytes[validMessageBytes.length - 2];
        _checkSum = new byte[]{xor};
        if (xor != realXor) {
            setErrorMessage("校验码不正确");
            logger.warn("T808Message 原始数据错误, 校验码错误 : " + Hex.encodeHexString((messageBytes)));
            throw new SinoXXException("000000");
        }

        try {
            // 03 数据长度, [首位标识符与检验字]
            int tempLen = validMessageBytes.length - 1 - 1 - 1;
            // 04 获取消息头 [12 无分包消息体为空]
            byte[] headerBytes = new byte[tempLen < 16 ? 12 : 16];
            System.arraycopy(validMessageBytes, 1, headerBytes, 0, headerBytes.length);
            header.readFromBytes(headerBytes);

            // 定位消息体数据指针
            int startPoint = 17;
            if (!header.getIsPackage()) {
                // 不分包则少4个字节的分包信息
                startPoint = 13;
            }
            if (header.getMessageSize() > 0) {
                // 消息体长度大于零 -> 解析消息体 sourceData
                byte[] sourceData = new byte[header.getMessageSize()];
                System.arraycopy(validMessageBytes, startPoint, sourceData, 0, sourceData.length);
                if (header.getIsPackage()) {
                    // 分包的消息体是纯数据不进行解析, 保留在消息中.
                    childPacket = new byte[header.getMessageSize()];
                    System.arraycopy(sourceData, 0, childPacket, 0, header.getMessageSize());
                } else {
                    // 消息数据解析
                    if (header.isEncryption()) {
                        // 消息加密
                        log.warn(String.format("808消息加密传输 => [ %s ]", hexMsg));
                        throw new SinoXXException("000000");
                    }
                    setMessageContents(T808MessageFactory.Create(header.getMessageType(), sourceData));
                }
            }
        } catch (Exception ex) {
            setErrorMessage("解析异常:" + ex.getMessage());
            logger.warn("T808Message 原始数据错误, 解析异常 : " + Hex.encodeHexString((messageBytes)));
            throw new SinoXXException("000000");
        }
    }

    /**
     * 获取校验和
     */
    private byte getCheckXor(byte[] data, int pos, int len) {
        byte A = 0;
        for (int i = pos; i < len; i++) {
            A ^= data[i];
        }
        return A;
    }

    /**
     * 将标识字符的转义字符还原
     */
    private byte[] unEscape(byte[] data) {
        MyBuffer buff = new MyBuffer();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0x7D) {
                if (data[i + 1] == 0x01) {
                    buff.put((byte) 0x7D);
                    i++;
                } else if (data[i + 1] == 0x02) {
                    buff.put((byte) 0x7E);
                    i++;
                }
            } else {
                buff.put(data[i]);
            }
        }
        byte[] a = buff.array();
        return a;
    }

    /**
     * 加入标示符的转义进行封装
     */
    private byte[] escape(byte[] data) {
        MyBuffer tmp = new MyBuffer();
        for (int j = 0; j < data.length; j++) {
            if (data[j] == 0x7D) {
                tmp.put((byte) 0x7D);
                tmp.put((byte) 0x01);
            } else if (data[j] == 0x7E) {
                tmp.put((byte) 0x7D);
                tmp.put((byte) 0x02);
            } else {
                tmp.put(data[j]);
            }
        }
        return tmp.array();
    }

    @Override
    public Integer getMessageType() {
        return getHeader().getMessageType();
    }

    @Override
    public String getSession() {
        return getOnlineNo();
    }

    @Override
    public String getHash() {
        return getOnlineNo();
    }

    @Override
    public byte[] getBytes() {
        return writeToBytes();
    }

}