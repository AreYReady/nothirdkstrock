package com.dqwl.optiontrade.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密算法
 *
 * @author long
 */
public class AesEncryptionUtil {
    // /** 算法/模式/填充 **/
    private static final String CipherMode = "AES/CBC/PKCS5Padding";
    private static final String KEY = "zyrt company";//密钥
    private static final String IV = "";//偏移量

    // /** 解密字节数组 **/
    private static byte[] decrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(AesEncryptionUtil.KEY);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, createIV(AesEncryptionUtil.IV));
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /** 创建密钥 **/
    private static SecretKeySpec createKey(String key) {
        byte[] data = new byte[16];
        if (key == null) {
            key = "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(key.getBytes());
            int length = baos.size();
            for (int i = length; i < 16; i++) {
                baos.write(0);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    private static IvParameterSpec createIV(String password) {
        byte[] data = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(password.getBytes());
            int length = baos.size();
            for (int i = length; i < 16; i++) {
                baos.write(0);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IvParameterSpec(data);
    }

    // /** 加密字节数据 **/
    private static byte[] encrypt(byte[] content) {
        try {
            SecretKeySpec key = createKey(AesEncryptionUtil.KEY);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, createIV(AesEncryptionUtil.IV));
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 加密(结果为16进制字符串)
     *
     * @param content 要加密的字符串
     * @return
     */
    public static String encrypt(String content) {
        byte[] data = null;
        try {
            data = content.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data);
        return parseByte2HexStr(data);
    }

    /**
     * 解密
     *
     * @param content 密文
     * @return
     */
    public static String decrypt(String content) {
        byte[] data = null;
        try {
            data = hex2bin(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = decrypt(data, KEY);
        if (data == null)
            return null;
        String result = null;
        result = new String(data);
        return result;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 十六进制转换字符串
     *
     * @param hex String 十六进制
     * @return String 转换后的字符串
     */
    private static byte[] hex2bin(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }

}