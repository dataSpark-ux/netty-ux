package com.wy.common.util;


import cn.hutool.core.util.HexUtil;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Buff 工具类
 *
 * @Author 2018-12-18
 */
public class ToolBuff {

    /**
     * 获取校验和
     */
    public static byte getCheckXor(byte[] data, int pos, int len) {
        byte A = 0;
        for (int i = pos; i < len; i++) {
            A ^= data[i];
        }
        return A;
    }

    /**
     * 十进制字符串转BCD数组 : 12位OnlineNo , 六字节 BCD - 十一位手机号补0
     */
    public static byte[] strToBCDBytes(String str) {
        if (str.length() == 11) {
            str = "0" + str;
        }
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    /**
     * BCD数组转十进制字符串
     */
    public static String bcdBytesToStr(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        String result = temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
        if (result.length() == 11) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * @param hex      消息的字节数组
     * @param onlineNo 要替换成的卡号
     */
    public static void convertOnlineNo(byte[] hex, String onlineNo) {
        byte[] onlineNoTemp = ToolBuff.strToBCDBytes(onlineNo);
        for (int i = 0; i < hex.length; i++) {
            if (i >= 5 && i <= 10) {
                // 替换OnlineNo号码
                hex[i] = onlineNoTemp[i - 5];
                continue;
            }
            if (i == hex.length - 2) {
                // 效验码
                hex[i] = ToolBuff.getCheckXor(hex, 1, hex.length - 2);
                continue;
            }
        }
    }

    /**
     * 字节序转化为十六进制字符串（大写）
     */
    public static String encodeHexString(byte[] bytes) {
        return HexUtil.encodeHexStr(bytes).toUpperCase();
    }

    /**
     * 十六进制字符串转化为字节序数组
     */
    public static byte[] decodeHexString(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return null;
        }
        hexStr = hexStr.toUpperCase();
        int length = hexStr.length() / 2;
        char[] hexChars = hexStr.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 字节序数组, 转换为二进制位字符串
     */
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            byte temp = b[i];
            String str = Integer.toBinaryString((temp & 0xFF) + 0x100).substring(1);
            result.append(str);
        }
        return result.toString();
    }


    /**
     * 十六进制字符串, 转化成 ASCII码字符串
     *
     * @param hex 十六进制字符串
     * @return
     */
    public static String hexToAscii(String hex) {
        String result = "";
        int len = hex.length() / 2;
        for (int i = 0; i < len; i++) {
            int tmp = Integer.valueOf(hex.substring(2 * i, 2 * i + 2), 16).intValue();
            result = result + (char) tmp;
        }
        return result;
    }

    /**
     * int 转化成十六进制字符串 => 0x0000
     *
     * @param data
     * @return
     */
    public static String toHexString(int data) {
        String tmp = Integer.toHexString(data);
        StringBuilder sb = new StringBuilder("00000000");
        sb.replace(8 - tmp.length(), 8, tmp);
        return sb.toString().toUpperCase();
    }

    /**
     * int 转化成十六进制字符串 => 0x0000
     *
     * @param data
     * @param byteNum
     * @return
     */
    public static String toHexString(long data, int byteNum) {
        int len = byteNum * 2;
        String tmp = Long.toHexString(data);
        while (tmp.length() < len) {
            tmp = "0" + tmp;
        }
        int start = tmp.length() - len;
        tmp = tmp.substring(start);
        return tmp.toUpperCase();
    }

    /**
     * BCD时间字节数据转化成时间对象
     *
     * @param date 6B BCD时间 [GMT+8 YY-MM-DD-hh-mm-ss]
     * @return
     */
    public static Date decodeBcdTime(byte[] date) {
        SimpleDateFormat f = new SimpleDateFormat("yyMMddHHmmss");
        String hexTime = bcdBytesToStr(date);
        try {
            return f.parse(hexTime);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取字节数据子数据, 并转化成字符串 (GBK)
     *
     * @param data
     * @param start
     * @param len
     * @return
     */
    public static String bytesToString(byte[] data, int start, int len) {
        try {
            return new String(data, start, len, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串流化 [GBK]
     *
     * @param str
     * @return
     */
    public static byte[] stringToBytes(String str) {
        try {
            return str.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 数据直接获取解析

    public static byte getByte(int start, byte[] bytes) {
        return (byte) toUInt(start, 1, bytes);
    }

    /**
     * 字节数组转成无符合short类型
     *
     * @param start
     * @param bytes
     * @return
     */
    public static short getUnsignedShort(int start, byte[] bytes) {
        return (short) toUInt(start, 2, bytes);
    }

    /**
     * @param start
     * @param bytes
     * @return
     */
    public static int getInt(int start, byte[] bytes) {
        return toUInt(start, 4, bytes);
    }

    /**
     * @param start   定位指针
     * @param lenByte 获取字节数 [1B\2B\4B]
     * @param bytes   字节数组
     * @return
     */
    private static int toUInt(int start, int lenByte, byte[] bytes) {
        int value = 0;
        int m = start + lenByte;
        for (int i = start; i < m; i++) {
            int shift = (m - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }

}
