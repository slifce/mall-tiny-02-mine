package com.macro.mall.tiny.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/23.
 */
/**
 DES加密介绍
 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现
 。
 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class DESUtil {

    public DESUtil() {
    }
    private static final String password="#a*c$3(z%g";
    private static final String forwardText="a70CE1K2";
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    //从数据库拿的身份证加密String要走这里
    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    /**
     * 用密钥加密
     * @param datasource byte[]
     * @param Key String
     * @return byte[]
     */
    public static String encryptWithKey(String datasource, String Key) {
        try{
            Map paramMap = new HashMap<>();
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(Key.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");//此处des是算法名
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            byte[] encypted = cipher.doFinal(datasource.getBytes());

            char[] buf = new char[encypted.length * 2];
            int index = 0;
            for(byte b : encypted) { // 利用zmhealt位运算进行转换
                buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
                buf[index++] = HEX_CHAR[b & 0xf];
            }

            String hexString =  new String(buf);
            return hexString;
            //现在，获取数据并加密
            //正式执行加密操作
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 解密
     * @param src byte[]
     * @param Key String
     * @return byte[]
     * @throws Exception
     */
    public static String decryptWithKey(String src, String Key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(Key.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return new String(cipher.doFinal(toBytes(src)));
    }



    /**
     * 加密
     * @param datasource String
     * @param key String
     * @return String
     */
    public static String encrypt(String datasource,String key) {
        try{
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(MD5Util.md5To32(key).substring(0, 8).getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(MD5Util.md5To32(key).substring(0, 8).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            String mingwen = forwardText+datasource;
            byte[] encypted = cipher.doFinal(mingwen.getBytes("UTF-8"));
            char[] buf = new char[encypted.length * 2];
            int index = 0;
            for(byte b : encypted) { // 利用位运算进行转换
                buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
                buf[index++] = HEX_CHAR[b & 0xf];
            }

            String hexString =  new String(buf);
            return hexString;
            //现在，获取数据并加密
            //正式执行加密操作
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解密
     * @param message String
     * @param key String
     * @return String
     * @throws Exception
     */
    public static String decrypt(String message,String key) throws Exception {
        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(MD5Util.md5To32(key).substring(0, 8).getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(MD5Util.md5To32(key).substring(0, 8).getBytes());
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte).substring(8);
    }

    /**
     * 加密
     * @param datasource String
     * @return String
     */
    public static String encrypt(String datasource) {
        if(datasource!=null && !"".equals(datasource)){
            try{
                Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
                DESKeySpec desKeySpec = new DESKeySpec(MD5Util.md5To32(password).substring(0, 8).getBytes());
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
                IvParameterSpec iv = new IvParameterSpec(MD5Util.md5To32(password).substring(0, 8).getBytes());
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                String mingwen = forwardText+datasource;
                byte[] encypted = cipher.doFinal(mingwen.getBytes("UTF-8"));
                char[] buf = new char[encypted.length * 2];
                int index = 0;
                for(byte b : encypted) { // 利用位运算进行转换
                    buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
                    buf[index++] = HEX_CHAR[b & 0xf];
                }

                String hexString =  new String(buf);
                return hexString;
                //现在，获取数据并加密
                //正式执行加密操作
            }catch(Throwable e){
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 解密
     * @param message String
     * @return String
     * @throws Exception
     */
    public static String decrypt(String message) throws Exception {
        if(message==null||message==""){
            return null;
        }
        try {
            byte[] bytesrc = convertHexString(message);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(MD5Util.md5To32(password).substring(0, 8).getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(MD5Util.md5To32(password).substring(0, 8).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte).substring(8);
        } catch (Exception e) {
            return message;//为加密的用户信息解密异常直接返回原数据
        }
    }


    public static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }
        return digest;
    }

    public static String decryptSubNumber(String subNumber) {
        String toReturn = "";
        try {
            toReturn = decrypt(subNumber);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return toReturn;
    }

    public static void main(String[] args) throws Exception {
        String mobilePhone = "18823786682";
        System.out.println("初始值mobilePhone = [" + mobilePhone + "]");
        String encryptMobilePhone = encrypt(mobilePhone);
        System.out.println("加密encryptMobilePhone = [" + encryptMobilePhone + "]");
        String decryptMobilePhone = decrypt(encryptMobilePhone);
        System.out.println("解密decryptMobilePhone = [" + decryptMobilePhone + "]");
    }
}
