package com.znh.siemens.utils;

/**
 * @Author JayNH
 * @Description TODO
 * @Date 2023/5/5 15:25
 * @Version 1.0
 */
public class FinalFunction {

    /**
     * 字节数组转成16进制表示格式的字符串
     * @param byteArray 需要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            // 0~F前面不零
            if ((byteArray[i] & 0xff) < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i])+" ");
        }
        return hexString.toString().toLowerCase();
    }

}
