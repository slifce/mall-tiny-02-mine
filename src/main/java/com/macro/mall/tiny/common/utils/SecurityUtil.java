package com.macro.mall.tiny.common.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by Administrator on 2020/7/23.
 */
public class SecurityUtil {
    private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static String signMD5(String cRequestMessage, String cKey) throws Exception {
        String tSource = cKey + cRequestMessage;
        return DigestUtils.md5Hex(tSource.getBytes("UTF-8"));
    }

    public static String Md5(String cPlainText) {
        StringBuffer tBuf = new StringBuffer();
        try {
            MessageDigest e = MessageDigest.getInstance("MD5");
            e.update(cPlainText.getBytes("utf-8"));
            byte[] tByte = e.digest();
            for (int j = 0; j < tByte.length; ++j) {
                int i = tByte[j];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    tBuf.append("0");
                }
                tBuf.append(Integer.toHexString(i));
            }
        } catch (Exception arg5) {
            logger.info(arg5.getMessage());
        }
        return tBuf.toString();
    }

    public static String SHA1(String cDecript) {
        StringBuffer tHexString = new StringBuffer();
        try {
            MessageDigest e = MessageDigest.getInstance("SHA-1");
            e.update(cDecript.getBytes());
            byte[] tMessageDigest = e.digest();
            for (int i = 0; i < tMessageDigest.length; ++i) {
                String tShaHex = Integer.toHexString(tMessageDigest[i] & 255);
                if (tShaHex.length() < 2) {
                    tHexString.append(0);
                }
                tHexString.append(tShaHex);
            }
        } catch (NoSuchAlgorithmException arg5) {
            logger.info(arg5.getMessage());
        }
        return tHexString.toString();
    }

    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptKey.getBytes());
        kgen.init(128, random);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(decryptKey.getBytes());
        kgen.init(128, random);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
        return encryptStr.isEmpty() ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }

    public static String base64Encode(byte[] bytes) {
        return (new BASE64Encoder()).encode(bytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
        return base64Code.isEmpty() ? null : (new BASE64Decoder()).decodeBuffer(base64Code);
    }

    public static String generateAESKey(String key) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes());
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        BASE64Encoder coder = new BASE64Encoder();
        return coder.encode(enCodeFormat);
    }

    public static void main(String[] args) throws Exception {
        String content = "我爱你";
        //System.out.println("加密前：" + content);
        String key = "123456";
        //System.out.println("加密密钥和解密密钥：" + key);
        String encrypt = aesEncrypt(content, key);
        System.out.println("加密后：" + encrypt);
        String decrypt = aesDecrypt(encrypt, key);
        System.out.println("解密后：" + decrypt);
        String zmKey = generateAESKey("123456");
        //System.out.println("加密后的key：" + zmKey);
    }
}
