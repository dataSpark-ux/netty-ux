package com.wy.common.util;


import org.apache.mina.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author wangyi
 */
public class Tools {
    public static long sendCount = 0L;

    /**
     * base64编码
     *
     * @param plainText
     * @return
     */
    public static String encodeStr(String plainText) {
        byte[] b = plainText.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        String s = new String(b);
        return s;
    }

    /**
     * b
     *
     * @param encodeStr
     * @return
     */
    public static String decodeStr(String encodeStr) {
        byte[] b = encodeStr.getBytes();
        Base64 base64 = new Base64();
        b = base64.decode(b);
        String s = new String(b);
        return s;
    }

    public static String CRC(String msg) {
        byte[] data = HexString2Bytes(msg);
        int crc = CRC(data);
        String str = ToHexString(crc, 2);
        return str;

    }

    public static int CRC(byte[] buffer) {
        int wTemp = 0;
        int crc = 0xffff;

        for (int i = 0; i < buffer.length; i++) {
            for (int j = 0; j < 8; j++) {
                wTemp = (short) (((buffer[i] << j) & 0x80) ^ ((crc & 0x8000) >> 8));

                crc <<= 1;

                if (wTemp != 0) {
                    crc ^= 0x1021;
                }
            }
        }

        return (crc);
    }

    public static long getSendCount() {
        if (sendCount > 1000000000L)
            sendCount = 1L;
        else {
            sendCount += 1L;
        }
        return sendCount;
    }

    public static String turnDataLength(String data, int length) {
        int data_length = data.length();
        for (int i = data_length; i < length; i++) {
            data = "0" + data;
        }
        return data;
    }

    public static String turnStrLength(String data, int length) {
        int data_length = data.length();
        for (int i = data_length; i < length; i++) {
            data = data + "0";
        }
        return data;
    }

    public static String ToHexString(byte[] bts) {
        StringBuilder strBuild = new StringBuilder();

        for (int i = 0; i < bts.length; i++) {
            strBuild.append(ToHexString(bts[i]));
        }
        return strBuild.toString();
    }

    //�ӿո� �����Ķ����
    public static String ToHexFormatString(byte[] bts) {
        StringBuilder strBuild = new StringBuilder();

        for (int i = 0; i < bts.length; i++) {
            strBuild.append(ToHexString(bts[i])).append(" ");
        }
        return strBuild.toString();
    }



    /**
     * 十六进制字符串, 转化成字节数组
     */
    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;

        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);

            b[i] = ((byte) (parse(c0) << 4 | parse(c1)));
            //int start = i * 2;
            //int c = Integer.parseInt(hexstr.substring(start, start + 2));
            //b[i] = (byte)c;
        }

        return b;
    }

    private static int parse(char c) {
        if (c >= 'a') {
            return c - 'a' + 10 & 0xF;
        }

        if (c >= 'A') {
            return c - 'A' + 10 & 0xF;
        }

        return c - '0' & 0xF;
    }

    public static String TurnISN(String str) {
        String str1 = "";
        byte[] b = (byte[]) null;
        try {
            b = str.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            System.out.println("�쳣��Ϣ(ControllerReport TurnISN)"
                    + e.getMessage());
        }
        System.out.println();
        int i = 0;
        for (int max = b.length; i < max; i++) {
            str1 = str1 + Integer.toHexString(b[i] & 0xFF);
        }
        return str1.toUpperCase();
    }

    // ������ת����16�����ַ����㲹��
    public static String ToHexString(byte data) {
        return Integer.toHexString(data & 0xff | 0x100).substring(1);
    }

    // ������ת����16�����ַ����㲹��
    public static String ToHexString(Short data) {
        return Integer.toHexString(data & 0xffff | 0x10000).substring(1);
    }

    public static String ToHexString(long val) {
        String tmp = Long.toHexString(val);
        StringBuilder sb = new StringBuilder("0000000000000000");
        sb.replace(16 - tmp.length(), 16, tmp);
        return sb.toString();
    }

    // ������ת����16�����ַ����㲹��
    public static String ToHexString(int data) {
        String tmp = Integer.toHexString(data);
        StringBuilder sb = new StringBuilder("00000000");
        sb.replace(8 - tmp.length(), 8, tmp);
        return sb.toString();
    }

    public static String ToHexString(long data, int byteNum) {
        int totalLen = byteNum * 2;
        String tmp = Long.toHexString(data);
        while (tmp.length() < totalLen) {
            tmp = "0" + tmp;
        }
        int start = tmp.length() - totalLen;
        tmp = tmp.substring(start);
        return tmp;
    }

    /**
     * ���ַ�ת����16���ƴ�
     */
    public static String ToHexString(String str) {
        String str1 = "";
        try {
            byte[] b = str.getBytes("gbk");
            int i = 0;
            int max = b.length;
            for (; i < max; i++) {
                str1 = str1 + Integer.toHexString(b[i] & 0xFF);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("�쳣��ϢToHexString"
                    + e.getMessage());
        }
        return str1;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    /**
     * ���ַ�ת����16���ƴ������Ȳ��㲹��
     */
    public static String ToHexString(String str, int length) {
        if (str == null)
            str = "";
        String str1 = "";
        byte[] b = (byte[]) null;
        try {
            b = str.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            System.out.println("�쳣��Ϣ(ControllerReport TurnISN)"
                    + e.getMessage());
        }
        System.out.println();
        int i = 0;
        int max = b.length;
        for (; i < max; i++) {
            str1 = str1 + Integer.toHexString(b[i] & 0xFF);
        }
        str1 = str1.toUpperCase();
        return turnStrLength(str1, length * 2);

    }


    public static String getStringFromHex(String str1) {
        try {
            str1 = new String(HexString2Bytes(str1), "gbk");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return str1;
    }

    public static int getYear(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(0, 4)).intValue();
    }

    public static int getMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(4, 6)).intValue();
    }

    public static int getDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(6, 8)).intValue();
    }

    public static int getHour(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(8, 10)).intValue();
    }

    public static int getMinute(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(10, 12)).intValue();
    }

    public static int getSeconds(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String year = format.format(date);
        return Integer.valueOf(year.substring(12, 14)).intValue();
    }

    public static String getDateBCDStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        return format.format(date);
    }

    public static String getHexDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String hexdate = format.format(date);

        String year = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(0, 4)).intValue(), 16), 4);
        String month = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(4, 6)).intValue(), 16), 2);
        String day = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(6, 8)).intValue(), 16), 2);
        String hour = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(8, 10)).intValue(), 16), 2);
        String minute = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(10, 12)).intValue(), 16), 2);
        String seconds = turnDataLength(Integer.toString(
                Integer.valueOf(hexdate.substring(12, 14)).intValue(), 16), 2);

        return day + month + year + hour + minute + seconds;
    }

    // ���ڲ���ָ�������ں�ʱ�䣬������ 1970 �� 1 �� 1 �� 00:00:00 GMT �����ĺ�����
    public static String getUTC(Date date) {

        //long dt = date.getTime()/1000;//

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);


        long dt = date.UTC(date.getYear(), date.getMonth(), date.getDay(),
                calendar.get(Calendar.HOUR_OF_DAY) - 8, date.getMinutes(), date.getSeconds());
        return ToHexString(dt / 1000, 8);
    }

    public static long myround(double num) {
        BigDecimal b = new BigDecimal(num);
        num = b.setScale(2, 4).longValue();
        return (long) num;
    }

    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(str);
        } catch (ParseException e) {
            System.out.println("�쳣��Ϣ��strToDate��:" + e.toString());
        }
        return null;
    }

    public static String encoderStringEscape(String strEscape) {
        strEscape = strEscape.toUpperCase();
        int byteNum = strEscape.length() / 2;
        StringBuilder sb = new StringBuilder();
        for (int m = 0; m < byteNum; m++) {
            int start = m * 2;

            String temp = strEscape.substring(start, start + 2);
            if (temp.equals("5A"))
                sb.append("5A02");
            else if (temp.equals("5B"))
                sb.append("5A01");
            else if (temp.equals("5E"))
                sb.append("5E02");
            else if (temp.equals("5D"))
                sb.append("5E01");
            else
                sb.append(temp);
        }
        // strEscape = strEscape.replaceAll("5A", "5A02");
        // strEscape = strEscape.replaceAll("5B", "5A01");
        // strEscape = strEscape.replaceAll("5E", "5E02");
        // strEscape = strEscape.replaceAll("5D", "5E01");
        return sb.toString();
    }

    public static String decoderStringEscape(String strEscape) {
        strEscape = strEscape.toUpperCase();
        strEscape = strEscape.replaceAll("5A01", "5B");
        strEscape = strEscape.replaceAll("5a01", "5B");
        strEscape = strEscape.replaceAll("5A02", "5A");
        strEscape = strEscape.replaceAll("5a02", "5A");
        strEscape = strEscape.replaceAll("5E01", "5D");
        strEscape = strEscape.replaceAll("5e01", "5D");
        strEscape = strEscape.replaceAll("5E02", "5E");
        strEscape = strEscape.replaceAll("5e02", "5E");
        return strEscape;
    }


    public static Timestamp convertToTimestamp(String message) {
        long time = Long.valueOf(message, 16).longValue();

        int year = (int) (time >> 26 & 0x3F) + 2000;
        int month = (int) (time >> 22 & 0xF);
        int day = (int) (time >> 17 & 0x1F);
        int hour = (int) (time >> 12 & 0x1F);
        int minute = (int) (time >> 6 & 0x3F);
        int second = (int) (time & 0x3F);
        Calendar cd = Calendar.getInstance();
        cd.set(year, month - 1, day, hour, minute, second);
        return new Timestamp(cd.getTimeInMillis());
    }

    public static String encrypt(int key, String str) {
        int m1 = 10000000;
        int a1 = 20000000;
        int c1 = 30000000;

        byte[] b = HexString2Bytes(str);
        int size = b.length;
        if (key == 0)
            key = 1;
        for (int i = 0; i < size; i++) {
            key = a1 * (key % m1) + c1;
            int tmp49_47 = i;
            byte[] tmp49_45 = b;
            tmp49_45[tmp49_47] = ((byte) (tmp49_45[tmp49_47] ^ (char) (key >> 20) & 0xFF));
        }
        return ToHexString(b);
    }

    public static void main(String[] args) {

        byte x = -85;

        int d = x & 0xff;

        short t = -23;
        String str = Integer.toHexString(t & 0xff);

        System.out.println(str);

        Date date = new Date();

        System.out.println(date.toString());
        System.out.println(date.getYear() + "," + date.getMonth() + "," + date.getDay() + "," +
                date.getHours() + "," + date.getMinutes() + "," + date.getSeconds());
        long dt = Date.UTC(date.getYear(), (date.getMonth()), 29,
                date.getHours() - 8, date.getMinutes(), date.getSeconds());

        Date date1 = new Date(dt);

        System.out.println(date1.toString());
    }

}
