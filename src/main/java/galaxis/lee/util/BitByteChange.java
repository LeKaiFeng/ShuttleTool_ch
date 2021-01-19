package galaxis.lee.util;

import org.junit.Test;

import java.util.Arrays;

/**
 * @Author: Lee
 * @Date: Created in 10:09 2020/8/18
 * @Description: TODO bit byte 相互转换，用于双货位控制逻辑释义
 */
public class BitByteChange {

    @Test
    public void ByteToBit() {
        byte m = 0x35;
        System.out.println("byte[] : "+ Arrays.toString(getBooleanArray(m)));
        System.out.println("String : "+byteToBit(m));
        //JDK 自带的方法，会忽略前面的0
        // System.out.println("JDK :"+Integer.toBinaryString(0x35));//结果：110101

        System.out.println("bitToByte : "+decodeBinaryString("00110101"));
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 根据各个Bit的值，返回byte的代码
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }
}
