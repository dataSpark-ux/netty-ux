package com.wy.common.util;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.buffer.IoBuffer;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 * Byte 字节序解析辅助类
 *
 * @Author 2018-12-12
 */
@Slf4j
public class BufferUtil {

    private IoBuffer buff;

    public BufferUtil() {
        buff = IoBuffer.allocate(1536);
        buff.mark();
    }

    public BufferUtil(int len) {
        buff = IoBuffer.allocate(len);
        buff.mark();
    }

    public BufferUtil(byte[] bytes) {
        if (bytes.length > 1024) {
            buff = IoBuffer.allocate(bytes.length + 100);
        } else {
            buff = IoBuffer.allocate(1024);
        }
        buff.mark();
        buff.put(bytes);
        buff.limit(bytes.length);
        buff.reset();
    }

    /**
     * 清除
     */
    public void clear() {
        // limit=capacity , position=0,重置mark
        buff.clear();
        // 取当前的position的快照标记mark
        buff.mark();
    }

    /**
     * 判断缓冲区是否还有数据
     */
    public boolean hasRemain() {
        return buff.remaining() > 0;
    }

    /********** ********** 将数据放入缓冲区 ********** **********/

    public void put(byte a) {
        buff.put(a);
    }

    public void put(short a) {
        buff.putShort(a);
    }

    public void put(byte[] byteData) {
        buff.put(byteData);
    }

    public void put(int intData) {
        buff.putInt(intData);
    }

    public void putShort(int intShortData) {
        buff.putShort((short) intShortData);
    }

    public void put(String str) {
        try {
            // US-ASCII
            byte[] b = str.getBytes("gbk");
            buff.put(b);
        } catch (Exception e) {
            log.error(String.format("gbk put"), e);
        }
    }

    public void put(String str, int len) {
        byte[] result = new byte[len];
        try {
            byte[] b = str.getBytes("gbk");
            System.arraycopy(b, 0, result, 0, b.length);
            for (int m = b.length; m < len; m++) {
                result[m] = 0;
            }
            buff.put(result);
        } catch (Exception e) {
            log.error(String.format("gbk put"), e);
        }
    }

    /********** ********** 从缓冲区中取值 ********** **********/

    /**
     * 取一定字节长度的byte字节数组
     */
    public byte[] gets(int len) {
        byte[] data = new byte[len];
        buff.get(data);
        return data;
    }

    /**
     * byte - 1 字节
     */
    public byte get() {
        return buff.get();
    }

    /**
     * Short 长度 - 2字节
     */
    public short getShort() {
        return buff.getShort();
    }

    /**
     * Int 长度 - 4字节
     */
    public int getInt() {
        return buff.getInt();
    }

    /**
     * hex转浮点
     */
    public float getFloat() {
        return buff.getFloat();
    }

    /**
     * 1、字节
     * 将data字节型数据转换为0~255 (0xFF 即BYTE)
     */
    public int getUnsignedByte() {
        return buff.get() & 0x0FF;
    }

    /**
     * 2、字
     * 将data字节型数据转换为0~65535 (0xFFFF 即 WORD)
     */
    public int getUnsignedShort() {
        short t = buff.getShort();
        return t & 0xffff;
    }

    /**
     * 3、双字 DWORD
     */
    public long getUnsignedInt() {
        return buff.getInt() & 0x0FFFFFFFF;
    }

    /**
     * 4、一定长度字节, 字节序转化为十六进制字符串
     */
    public String encodeHexString(int len) {
        byte[] bytes = this.gets(len);
        StringBuilder bcd = new StringBuilder();
        for (int m = 0; m < len; m++) {
            bcd.append(String.format("%02X", bytes[m]));
        }
        return bcd.toString();
    }

    /**
     * 一定长度字节的GBK转码后字符串
     */
    public String getString(int len) {
        return getString(len, "GBK");
    }

    public String getString(int len, String charsetName) {
        try {
            return buff.getString(len, Charset.forName(charsetName).newDecoder());
        } catch (CharacterCodingException e) {
            gets(len);
            log.error(String.format("GBK转码后字符串出错 => [ %s ]", ToolBuff.encodeHexString(this.array())), e);
//            throw new SinoXXException("000011");
            return "";
        }
    }

    /**
     * 字符串
     */
    public String getString() {
        try {
            return buff.getString(Charset.forName("GBK").newDecoder());
        } catch (CharacterCodingException e) {
            log.error(String.format("GBK转码后字符串出错 => [ %s ]", ToolBuff.encodeHexString(this.array())), e);
//            throw new SinoXXException("000011");
            return "";
        }
    }

    public byte[] array() {
        int pos = buff.position();
        byte[] data = new byte[pos];
        buff.reset();
        buff.get(data);
        return data;
    }

    /**
     * 设置数据定位指针
     *
     * @param position
     */
    public void setPosition(int position) {
        buff.position(position);
    }

    /**
     * 获取一定长度字节数组转化的二进制位字符串 ,字符串长度为字节长度8倍
     */
    public String getByteArrToBinStr(int len) {
        byte[] b = gets(len);
        return ToolBuff.byteArrToBinStr(b);
    }

    /**
     * 取一个值的前后四位的值
     *
     * @param b xu
     * @return 返回字节数组 0 为前四位值 、1 为后四位值
     */
    public static byte[] getPreAndAfter(byte b) {
        byte[] result = new byte[2];
        // 00001111用于取出后四位
        byte num = 0xF;
        // 将高位移到低位再取值
        byte behind = (byte) ((b >> 4) & num);
        // 与掉前四位
        byte front = (byte) (b & num);
        // 转化为字节字符
        // char[ ] arr=new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        // "前四位:"+arr[front]+" 后四位:"+arr[behind];
        result[0] = behind;
        result[1] = front;
        return result;
    }

}
