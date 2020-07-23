package com.macro.mall.tiny.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.tiny.common.api.StaticEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2020/7/23.
 */
public class OSSUtil {

//    public static StaticEntity ossUpload(){
//        StaticEntity result = new StaticEntity();
//        Map map = new HashMap();
//        map.put("fileName","ceShi.jpg");
//        try {
//            //模拟流传递
//            String absolutePath = "https://images.zhongmin.cn/images/2018/320/11743.jpg";
//            URL url = new URL(absolutePath);
//            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
//            InputStream is = urlconn.getInputStream();//通过输入流获取图片数据
//            String ENDPOINT = "http://oss-cn-szfinance.aliyuncs.com";
//            String ACCESS_KEY_ID = "LTAI4FjvDaQ5HKCkf1BJc19R";
//            String ACCESS_KEY_SECRET = "qdH1ku2OPk4g6IRpzfSYXFxqBuK1fu";
//            String BUCKETNAME = "hgosstest2";
//            String DISKNAME = "zmbxw/";
//
//            OSSClient client=new OSSClient(ENDPOINT,ACCESS_KEY_ID, ACCESS_KEY_SECRET);
//            try {
//                PutObjectResult putResult = client.putObject(new PutObjectRequest(BUCKETNAME, map.get("filename").toString(), is));
//                //解析结果
//                if(putResult!=null){
//                    // 判断阿里云图片上否上传成功
//                    URL aurl3 = new URL(absolutePath);
//                    HttpURLConnection urlconn3 = (HttpURLConnection) aurl3.openConnection();
//                    InputStream is3 = urlconn3.getInputStream();
//                    client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
//                    Date expiration = new Date(new Date().getTime() + 3600 * 10000);
//                    URL u = client.generatePresignedUrl(BUCKETNAME, map.get("filename").toString(), expiration);
//                    Map<String ,Object> oosParams = new HashMap<String ,Object>();
//                    oosParams.put("u", u.toString());
//                    oosParams.put("isSuccess", 0);
//                    byte[] data = new byte[1024];
//                    try {
//                        if (is3.read(data) != -1) {
//                            oosParams.put("isSuccess", 1);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (is3 != null) {
//                            try {
//                                is3.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    if(!"1".equals(oosParams.get("isSuccess").toString())){
//                        result.setCode(502);
//                        result.setMessage("上传阿里云OSS服务器，图片上传失败");
//                        return result;
//                    }
//                    result.setCode(200);
//                    result.setMessage("上传阿里云OSS服务器正常");
//                }else{
//                    result.setCode(501);
//                    result.setMessage("上传阿里云OSS服务器异常");
//                }
//            } catch (Exception e) {
//                result.setCode(501);
//                result.setMessage("上传阿里云OSS服务器异常.");
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//            return new StaticEntity<>(502, "OCR请求异常");
//        }
//        return null;
//    }
}
