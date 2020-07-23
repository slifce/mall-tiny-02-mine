package com.macro.mall.tiny.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by Administrator on 2020/7/23.
 */
public class MD5Util {

    public static final String signKey = "zm123InsureWine";

    public static void main(String[] args) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String md5Value = MD5("123456");
        System.out.println("md5Value = [" + md5Value + "]");
        String key = "tkcwuhan";
        String value = "z0tf9kzDZvWlbm8io4ynFVDZSWqPDd834h"+
                "FF6QL8TdVW4qkV8VAnmw1IeIXTilpxkw%2FP2THN2u%2BA%0AGd"+
                "kahC2qCOjnHv%2FHALiO9psbwRxjfrPniywI6nP1Zzb9y"+
                "q%2FM31CvCdPid%2BybxqZu6kA%2BJpF%2BZCpd%0AupalabQOHJTr3P"+
                "GajfroqKMTlcoq%2BcmXBv9S4eI2R4oZjHE%2FDufKz2k9m2hKNiZ1HxMn"+
                "74%2FwknOJ%0ABhbIRJcs%2B%2ByajVIhAjZJVCetyfpS58dfpqE4hderBEb"+
                "XDAiM9LAjcQeYucE2XF1yISe6%2F99LBay6%0A0k5QEEbxpLM%2B66JjZptwqQ"+
                "QYI4XuLIkOMUibGlImPI3PSrZPY4D8YCdxL6ycEpv98VZeON1Ke6yX%0Ao4%2FUI"+
                "MfO7bKoN6X5g1UJAUMwjT1wHSPKI7RQHtWwIWPRPR4D2bIJMNEeyQ%3D%3D";
        String ddd = URLDecoder.decode(value, "utf-8");
        System.out.println("URLDecoder解码------" + ddd);
        String decode = decryptECB(key,ddd);
        System.out.println(decode);

    }

    /**
     *
     * @param plainText
     *            明文
     * @return 32位密文
     */
    public static String md5To32(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    //MD5forSMS
    public final static String MD5(String data) {
        //用于加密的字符
        char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = data.getBytes("UTF-8");

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }

            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            return null;
        }
    }

    public final static String MD5(String data,String charsetName) {
        //用于加密的字符
        char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = data.getBytes(charsetName);

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }

            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 获取短信验证签名
     * @param content 短信内容
     * @param Key1
     * @param Key2
     * @return 验证签名
     */
    public static String getCode(String content, String Key1, String Key2) {
        String toReturn = MD5Util.MD5(MD5Util.MD5(content + Key1) + Key2);
        return toReturn;
    }

    /**
     * 支付、财务流接口签名加密
     * @param params 参数map
     * @param Key 签名密钥
     * @return
     */
    public static String getSignForPay(Map<String, String> params, String Key){
        String pa = JSON.toJSONString(params, SerializerFeature.MapSortField);
        return MD5Util.MD5(pa + Key);
    }

    public static String GetMd5_Tpy(String dataSource) {
        String md51 = MD5("zhongmin_tpy").toLowerCase();
        return MD5(md51+dataSource).toLowerCase();
    }

    /**
     *
     * @Description: 康泰2020加密ECB加密
     */
    public static String createEncryption(String key,String value) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] binaryData = cipher.doFinal(value.getBytes("UTF-8"));
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(binaryData);
        }catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }
    /**
     *
     * @Description: 泰康2020MD5加密方式
     */
    public static String getMD5Mac(String stSourceString,String decode) {
        String mystring = "";
        byte getbyte[];
        try {
            getbyte = getMD5Mac(stSourceString.getBytes(decode));
            mystring = bintoascii(getbyte);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return mystring;
    }
    /**
     *
     * @Description: 泰康2020MD5加密方式
     */
    public static byte[] getMD5Mac(byte [] bySourceByte) {
        byte[] byDisByte;
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(bySourceByte);
            byDisByte = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return (byDisByte);

    }
    /**
     *
     * @Description: 泰康2020ECB加密方式
     */
    public static String bintoascii(byte[] bySourceByte) {
        int len,i;
        byte tb;
        char high,tmp,low;
        String result = new String();
        len = bySourceByte.length;
        for(i = 0;i < len;i++) {
            tb = bySourceByte[i];

            tmp = (char)((tb >>> 4) & 0x000f);
            if(tmp >= 10)
                high = (char)('a' + tmp - 10);
            else
                high = (char)('0' + tmp);
            result += high;
            tmp = (char)(tb & 0x000f);
            if(tmp >= 10) {
                low = (char) ('a' + tmp - 10);
            }else {
                low = (char)('0' + tmp);
            }

            result += low;
        }
        return result;
    }

    /**
     *
     * @Description: ECB解密
     */
    public static String decryptECB(String key,String value) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] binaryValue = decoder.decodeBuffer(value);
            SecretKey secreatKey = new SecretKeySpec(key.getBytes("UTF-8"),"DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,secreatKey);
            byte[] data = cipher.doFinal(binaryValue);
            return new String (data,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

