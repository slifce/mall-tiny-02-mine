package com.macro.mall.tiny.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.macro.mall.tiny.common.api.StaticEntity;
import org.apache.commons.io.IOUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 类说明 sftp工具类
 * Created by Administrator on 2020/7/23.
 */
public class SFTPUtil {
    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    private ChannelSftp sftp;

    private Session session;
    /** SFTP 登录用户名*/
    private String username;
    /** SFTP 登录密码*/
    private String password;
    /** 私钥 */
    private String privateKey;
    /** SFTP 服务器地址IP地址*/
    private String host;
    /** SFTP 端口*/
    private int port;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtil(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtil(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    public SFTPUtil(){}


    /**
     * 连接sftp服务器
     */
    public void login(){
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                jsch.addIdentity(privateKey);// 设置私钥
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接 server
     */
    public void logout(){
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     * @param basePath  服务器的基础路径
     * @param directory  上传到该目录
     * @param sftpFileName  sftp端文件名
     * @param input   输入流
     */
    public void upload(String basePath,String directory, String sftpFileName, InputStream input) throws SftpException{
        try {
            sftp.cd(basePath);
            sftp.cd(directory);
        } catch (SftpException e) {
            //目录不存在，则创建文件夹
            String [] dirs=directory.split("/");
            String tempPath=basePath;
            for(String dir:dirs){
                if(null== dir || "".equals(dir)) continue;
                tempPath+="/"+dir;
                try{
                    sftp.cd(tempPath);
                }catch(SftpException ex){
                    sftp.mkdir(tempPath);
                    sftp.cd(tempPath);
                }
            }
        }
        sftp.put(input, sftpFileName);  //上传文件
    }


    /**
     * 下载文件。
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     */
    public void download(String directory, String downloadFile, String saveFile) throws SftpException, FileNotFoundException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载的文件名
     * @return 字节数组
     */
    public byte[] download(String directory, String downloadFile) throws SftpException, IOException{
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);

        return fileData;
    }


    /**
     * 删除文件
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }


    /**
     * 列出目录下的文件
     * @param directory 要列出的目录
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    /**
     * 调用方法
     * @param userName
     * @param password
     * @param ip
     * @param port
     * @param workDir
     * @throws SftpException
     * @throws IOException
     */
    public static void uploadLocalFileToSftp(String userName, String password, String ip, int port, String workDir, String url, String fileName, String directory) throws SftpException, IOException {
        SFTPUtil sftp = new SFTPUtil(userName, password, ip, port);
        sftp.login();
        File file = new File(url);
        InputStream is = new FileInputStream(file);
        sftp.upload(workDir,directory, fileName, is);
        sftp.logout();
    }

    public static StaticEntity<String> uploadHttpUrlToSftp(String userName, String password, String ip, int port, String workDir, String url, String fileName, String directory) throws SftpException, IOException {
        StaticEntity<String> result = new StaticEntity<>(200, "success");
        try {
            SFTPUtil sftp = new SFTPUtil(userName, password, ip, port);
            sftp.login();
            URL urlContent = new URL(url);
            URLConnection connection = urlContent.openConnection();
            InputStream is = connection.getInputStream();
            sftp.upload(workDir,directory, fileName, is);
            sftp.logout();
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setCode(501);
        }
        return result;
    }

    public static StaticEntity<String> uploadHttpUrlToftpContainChinese(String userName, String password, String ip, int port, String workDir, String url, String fileName, String directory) throws SftpException, IOException {
        StaticEntity<String> result = new StaticEntity<>(200, "success");
        try {
            SFTPUtil sftp = new SFTPUtil(userName, password, ip, port);
            sftp.login();
            URL urlContent = new URL(url);
            URLConnection connection = urlContent.openConnection();
            InputStream is = connection.getInputStream();
            sftp.upload(workDir,directory, new String(fileName.getBytes("GBK")), is);
            sftp.logout();
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setCode(501);
        }
        return result;
    }

    public static void postHttp1(){
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"GBK\"?>");
        sb.append("<PackageList>");
        sb.append("    <Package>");
        sb.append("        <Header>");
        sb.append("            <RequestType>32</RequestType>");
        sb.append("            <UUID>8c0ef649-4479-4dcf-b7ba-7b1163780112</UUID>");
        sb.append("            <SendTime>2019-01-31 20:33:17</SendTime>");
        sb.append("            <AppPlatform>ZMBXJJFINANCE</AppPlatform>");
        sb.append("            <ComId>10002</ComId>");
        sb.append("            <OrderSerial>iyb20190621112699</OrderSerial>");
        sb.append("            <ComSerial/>");
        sb.append("            <Asyn/>");
        sb.append("            <ReturnUrl/>");
        sb.append("            <UserId>iyb20190621112699</UserId>");
        sb.append("        </Header>");
        sb.append("        <Request>");
        sb.append("            <Media>");
        sb.append("                <OrderId>iyb20190621112699</OrderId>");
        sb.append("		           <PolicyNo></PolicyNo>");
        sb.append("		           <BizType>TB</BizType>");
        sb.append("		           <SubBizType>010701</SubBizType>");
        sb.append("		           <BizId>19020213441677182244</BizId>");
        sb.append("		           <CertiType>1</CertiType>");
        sb.append("		           <FileList>");
        sb.append("                    <File>");
        sb.append("                        <FilePath>/TB/2019/06/21/010701/19020213441677182244/1.jpg</FilePath>");
        sb.append("                        <FileNo>1</FileNo>");
        sb.append("                        <PolicyHolder>1</PolicyHolder>");
        sb.append("                    </File>");
        sb.append("                    <File>");
        sb.append("                        <FilePath>/TB/2019/06/21/010701/19020213441677182244/2.jpg</FilePath>");
        sb.append("                        <FileNo>2</FileNo>");
        sb.append("                        <PolicyHolder>1</PolicyHolder>");
        sb.append("                    </File>");
        sb.append("		           </FileList>");
        sb.append("            </Media>");
        sb.append("        </Request>");
        sb.append("    </Package>");
        sb.append("</PackageList>");
        String encryptMsg = sb.toString();
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("encryptMsg", encryptMsg);
        String bohai_sftp_key = "123456";
        String sign = SecurityUtil.Md5(sb.toString()+bohai_sftp_key).toLowerCase();
        String result2 = null;
        try {
            result2 = HttpClientUtil.postDataUTF8("http://ds-test.bohailife.net/ebiz/third/open.action?action=mediaUpload&AppPlatform=ZMBXJJFINANCE"+"&sign="+sign, paramMap);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.out.println("result2:"+result2);
        Document document1;
        try {
            document1 = DocumentHelper.parseText(result2);
            Element rootElement = document1.getRootElement();
            @SuppressWarnings("unchecked")
            Iterator<Element> iterator = rootElement.elementIterator();
            while (iterator.hasNext()){
                Element firstLevel = (Element) iterator.next();
                List<Attribute> attributes = firstLevel.attributes();
                Iterator<Element> iterator1 = firstLevel.elementIterator();
                while (iterator1.hasNext()){
                    Element secondLevel = (Element) iterator1.next();
                    if(secondLevel.getName()!=null){
                        if("Response".equals(secondLevel.getName())){
                            Iterator<Element> iterator2 = secondLevel.elementIterator();
                            while(iterator2.hasNext()){
                                Element thirdLevel = (Element) iterator2.next();
                                if(thirdLevel.getName()!=null){
                                    if("Media".equals(thirdLevel.getName())){
                                        Iterator<Element> iterator3 = thirdLevel.elementIterator();
                                        while(iterator3.hasNext()){
                                            Element fourthLevel = (Element) iterator3.next();
                                            if("IsSuccess".equals(fourthLevel.getName())){
                                                System.out.println("******--------:"+fourthLevel.getStringValue());
                                                if("1".equals(fourthLevel.getStringValue())){
                                                    System.out.println("通过："+fourthLevel.getStringValue());
                                                }else{
                                                    System.out.println("未通过："+fourthLevel.getStringValue());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static boolean post2(){
        String message = "<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?><EbizReq><EbizHead><SerialNo>1595413026358</SerialNo><TransType>1001</TransType><SourceCode>ZM01</SourceCode><BusinessNo>201908141647426524810754</BusinessNo></EbizHead><TransInfo><ProposalPrtNo>1300100000252925</ProposalPrtNo><ContNo/><ESViewList><ESView><PageNum>8</PageNum><PageList><Page><CostomerType>00</CostomerType><ImageName>1300100000252925_11010119830101008X_测试妻_00_1.jpg</ImageName><PageCode>1</PageCode><ImagePath>/imageupload/20200722/1300100000252925/00/1300100000252925_11010119830101008X_测试妻_00_1.jpg</ImagePath><CardType>01</CardType><CardNo>11010119830101008X</CardNo><CardName>测试妻</CardName></Page><Page><CostomerType>00</CostomerType><ImageName>1300100000252925_11010119830101008X_测试妻_00_2.jpg</ImageName><PageCode>2</PageCode><ImagePath>/imageupload/20200722/1300100000252925/00/1300100000252925_11010119830101008X_测试妻_00_2.jpg</ImagePath><CardType>01</CardType><CardNo>11010119830101008X</CardNo><CardName>测试妻</CardName></Page><Page><CostomerType>01</CostomerType><ImageName>1300100000252925_110101198201010090_测试丈_01_1.jpg</ImageName><PageCode>1</PageCode><ImagePath>/imageupload/20200722/1300100000252925/01/1300100000252925_110101198201010090_测试丈_01_1.jpg</ImagePath><CardType>01</CardType><CardNo>110101198201010090</CardNo><CardName>测试丈</CardName></Page><Page><CostomerType>01</CostomerType><ImageName>1300100000252925_110101198201010090_测试丈_01_2.jpg</ImageName><PageCode>2</PageCode><ImagePath>/imageupload/20200722/1300100000252925/01/1300100000252925_110101198201010090_测试丈_01_2.jpg</ImagePath><CardType>01</CardType><CardNo>110101198201010090</CardNo><CardName>测试丈</CardName></Page><Page><CostomerType>02</CostomerType><ImageName>1300100000252925_110101200201010031_测孩一_02_1.jpg</ImageName><PageCode>1</PageCode><ImagePath>/imageupload/20200722/1300100000252925/02/1300100000252925_110101200201010031_测孩一_02_1.jpg</ImagePath><CardType>01</CardType><CardNo>110101200201010031</CardNo><CardName>测孩一</CardName></Page><Page><CostomerType>02</CostomerType><ImageName>1300100000252925_110101200201010031_测孩一_02_2.jpg</ImageName><PageCode>2</PageCode><ImagePath>/imageupload/20200722/1300100000252925/02/1300100000252925_110101200201010031_测孩一_02_2.jpg</ImagePath><CardType>01</CardType><CardNo>110101200201010031</CardNo><CardName>测孩一</CardName></Page><Page><CostomerType>02</CostomerType><ImageName>1300100000252925_11010120030101008X_测孩二_02_1.jpg</ImageName><PageCode>1</PageCode><ImagePath>/imageupload/20200722/1300100000252925/02/1300100000252925_11010120030101008X_测孩二_02_1.jpg</ImagePath><CardType>01</CardType><CardNo>11010120030101008X</CardNo><CardName>测孩二</CardName></Page><Page><CostomerType>02</CostomerType><ImageName>1300100000252925_11010120030101008X_测孩二_02_2.jpg</ImageName><PageCode>2</PageCode><ImagePath>/imageupload/20200722/1300100000252925/02/1300100000252925_11010120030101008X_测孩二_02_2.jpg</ImagePath><CardType>01</CardType><CardNo>11010120030101008X</CardNo><CardName>测孩二</CardName></Page></PageList></ESView></ESViewList></TransInfo></EbizReq>";
        String fosun_ftp_url = "http://sit-impthird.fuis.fosun-uhi.com:9292/third/ebiz-entry/ebiz/antiMoneyLaunderingImageUpload.do?action=dealBiz";
        String fosun_ftp_key = "FOSUN2016";
        String fosun_ftp_key_aes = "ewealth";
        String ebiz_sign = SecurityUtil.Md5(message+fosun_ftp_key_aes).toLowerCase();
        String request_xml = null;
        try {
            request_xml = SecurityUtil.aesEncrypt(message, fosun_ftp_key);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("request_xml", request_xml);
        paramMap.put("ebiz_sign", ebiz_sign);//签名
        String result = null;
        try {
            result = HttpClientUtil.postDataUTF8(fosun_ftp_url, paramMap);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        String newStr = result.replaceAll(" ","\n");
        net.sf.json.JSONObject resultObj = Xml2Json.xml2JSON(newStr);
        net.sf.json.JSONObject object = (net.sf.json.JSONObject) resultObj.get("EbizRes");
        //解析结果集，获取地址与核保单号
        if(object.get("ResHead")!=null){
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(object.getString("ResHead"));
            JsonArray array=je.getAsJsonArray();
            for(int i = 0 ; i<array.size();i++){
                JsonObject subObject=array.get(i).getAsJsonObject();
                if(subObject.get("ResultCode")!=null && subObject.get("ResultCode").getAsString().equals("01")){
                    return true;
                }
            }
        }
        return false;
    }



    //上传文件测试
    public static void main(String[] args) throws SftpException, IOException {
        //postHttp1();uploadHttpUrlToftp
        //uploadLocalFileToSftp("zmbx_uat", "zmbx_uat_$RFV", "112.74.242.200", 60022, "/zmbxfinance/TB/","D://sfzzm.png","zm_test1_sftp.jpg","201340450211");
        //uploadHttpUrlToSftp("zmbx_uat", "zmbx_uat_$RFV", "112.74.242.200", 60022, "/zmbxfinance/TB/","https://images.zhongmin.cn/images/2018/320/11743.jpg","1.png","/zmbxfinance/TB/2019/11/27/013501/201340450211/");
        //uploadHttpUrlToftpContainChinese("ZM01_test", "8vx88nSH", "139.224.149.231", 5023, "/imageupload/","https://images.zhongmin.cn/images/2018/320/11743.jpg","1300100000144825_430401198305310611_小欧_00_1.jpg","/20200723/1300100000144825/00/");
        ///imageupload/20191220/1300100000144825/00/1300100000144825_430401198305310611_小欧_00_1.jpg
        post2();
    }
}
