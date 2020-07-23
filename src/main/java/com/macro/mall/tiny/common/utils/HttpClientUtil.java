package com.macro.mall.tiny.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by Administrator on 2020/7/23.
 */
public class HttpClientUtil {
    private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/tHtml";
    public static final String CONTENT_TYPE_APPLICATION_FORM = "application/x-www-form-urlencoded";
    private static HttpClient httpClient = null;
    private static final String HTTP_METHOD_GET = "GET";
    private static final String CONTENT_TYPE = "text/tHtml;charset=UTF-8";

    public static String originalPostData(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        UTF8PostMethod tProcPost = new UTF8PostMethod(cUrlStr);
        Set tKeySet = cParamMap.keySet();
        for (Iterator i$ = tKeySet.iterator(); i$.hasNext();) {
            Object tKey = i$.next();
            String tKeyValue = String.valueOf(tKey);
            String tValue = (String) cParamMap.get(tKeyValue);
            if (tValue == null) {
                tValue = "";
            }
            tProcPost.addParameter(tKeyValue, tValue);
        }
        httpClient.executeMethod(tProcPost);
        byte[] tDataArr = null;
        String tResult = null;
        try {
            tDataArr = tProcPost.getResponseBody();
            tResult = new String(tDataArr, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tResult;
    }

    public static String sendPaymentXml(Map<String, String> cParamMap, String cXml, String cUrl) throws Exception {
        cParamMap.put("dataXml", cXml);
        return originalPostData(cUrl, cParamMap);
    }

    public static String postData(String cUrlStr, Map<String, String> cParamMap, String cCharsetType) throws Exception {
        UTF8PostMethod tProcPost = new UTF8PostMethod(cUrlStr);
        Set tKeySet = cParamMap.keySet();
        for (Iterator i$ = tKeySet.iterator(); i$.hasNext();) {
            Object tKey = i$.next();
            String tKeyValue = String.valueOf(tKey);
            String tValue = (String) cParamMap.get(tKeyValue);
            if (tValue == null) {
                tValue = "";
            }
            tProcPost.addParameter(tKeyValue, tValue);
        }
        logger.info("HTTP 01");
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000);
        int tStatusCode = httpClient.executeMethod(tProcPost);
        logger.info(new StringBuilder().append("HTTP 02").append(tStatusCode).toString());
        InputStream tResInputStream = null;
        StringBuffer tHtml = new StringBuffer();
        try {
            tResInputStream = tProcPost.getResponseBodyAsStream();
            BufferedReader tReader = new BufferedReader(new InputStreamReader(tResInputStream, cCharsetType));
            String tTempBf = null;
            while ((tTempBf = tReader.readLine()) != null) {
                tHtml.append(tTempBf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tResInputStream != null) {
                tResInputStream.close();
            }
        }
        logger.info("HTTP 03");
        return new String(tHtml.toString().getBytes(cCharsetType), cCharsetType);
    }

    public static String postData(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        return postData(cUrlStr, cParamMap, "GBK");
    }

    public static String postDataUTF8(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        return postData(cUrlStr, cParamMap, "UTF-8");
    }

    public static String postJsonData(String cUrlStr, String cXmlData) throws Exception {
        return postJsonData(cUrlStr, cXmlData, "UTF-8");
    }

    public static String postJsonData(String cUrlStr, String cXmlData, String cCharsetType) throws Exception {
        UTF8PostMethod tPost = new UTF8PostMethod(cUrlStr);
        tPost.setRequestBody(cXmlData);
        httpClient.executeMethod(tPost);
        return getResponseXml(tPost.getResponseBodyAsStream());
    }

    public static String originalGetData(String cUrlStr) throws Exception {
        return originalGetData(cUrlStr, new HashMap(), "UTF-8");
    }

    public static String originalGetData(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        return originalGetData(cUrlStr, cParamMap, "UTF-8");
    }

    public static String originalGetData(String cUrlStr, Map<String, String> cParamMap, String cCharsetType)
            throws Exception {
        int i = 0;
        char tChar = '\0';
        Iterator i$;
        if (cParamMap.size() > 0) {
            Set tKeySet = cParamMap.keySet();
            for (i$ = tKeySet.iterator(); i$.hasNext();) {
                Object tKey = i$.next();
                i = cUrlStr.indexOf("?");
                if (i > 0)
                    tChar = '&';
                else {
                    tChar = '?';
                }
                String tKeyValue = String.valueOf(tKey);
                String tValue = (String) cParamMap.get(tKeyValue);
                if (tValue == null)
                    tValue = "";
                else {
                    tValue = URLEncoder.encode(tValue, cCharsetType);
                }
                cUrlStr = new StringBuilder().append(cUrlStr).append(tChar).append(tKeyValue).append("=").append(tValue)
                        .toString();
            }
        }
        GetMethod tProcGet = new GetMethod(cUrlStr);
        httpClient.executeMethod(tProcGet);
        String tResult = null;
        try {
            tResult = tProcGet.getResponseBodyAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tResult;
    }

    public static String getResponseXml(InputStream cInputStream) throws Exception {
        BufferedReader tBufferedReader = new BufferedReader(new InputStreamReader(cInputStream, "UTF-8"));
        StringBuffer tBuffer = new StringBuffer();
        String tDemo = "";
        while ((tDemo = tBufferedReader.readLine()) != null) {
            tBuffer.append(tDemo);
        }
        String tXml = tBuffer.toString();
        return tXml;
    }

    public static String postDataWithUTF8(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        return postData(cUrlStr, cParamMap, "UTF-8");
    }

    public static String getDataUrl(String cUrlStr, Map<String, String> cParamMap) throws Exception {
        int i = 0;
        char tChar = '\0';
        Iterator i$;
        if (cParamMap.size() > 0) {
            Set tKeySet = cParamMap.keySet();
            for (i$ = tKeySet.iterator(); i$.hasNext();) {
                Object tKey = i$.next();
                i = cUrlStr.indexOf("?");
                if (i > 0)
                    tChar = '&';
                else {
                    tChar = '?';
                }
                String tKeyValue = String.valueOf(tKey);
                String tValue = (String) cParamMap.get(tKeyValue);
                if (tValue == null) {
                    tValue = "";
                }
                cUrlStr = new StringBuilder().append(cUrlStr).append(tChar).append(tKeyValue).append("=").append(tValue)
                        .toString();
            }
        }
        return cUrlStr;
    }

    public static String tozhCN(String cUnicode) {
        StringBuffer tGbk = new StringBuffer();
        String[] tHex = cUnicode.split("\\\\u");
        for (int i = 1; i < tHex.length; ++i) {
            int tDataArr = Integer.parseInt(tHex[i], 16);
            tGbk.append((char) tDataArr);
        }
        return tGbk.toString();
    }

    public static String toUnicode(String cZhStr) {
        StringBuffer tUnicode = new StringBuffer();
        for (int i = 0; i < cZhStr.length(); ++i) {
            char tChar = cZhStr.charAt(i);
            tUnicode.append(new StringBuilder().append("\\u").append(Integer.toHexString(tChar)).toString());
        }
        return tUnicode.toString();
    }

    public static String getRequest(String cUrl, String cAueryString, String cCharSet, String cContentType) {
        String tEncode = (StringUtils.isNotEmpty(cCharSet)) ? cCharSet : "UTF-8";
        String tType = (StringUtils.isNotEmpty(cContentType)) ? cContentType : "text/tHtml";

        String tEncodeUrl = cUrl;
        logger.info(new StringBuilder().append("tEncodeUrl:").append(tEncodeUrl).toString());
        try {
            tEncodeUrl = URIUtil.encodeQuery(cUrl, tEncode);
        } catch (URIException e) {
            e.printStackTrace();
        }
        logger.info(new StringBuilder().append("tEncodeUrl----------:").append(tEncodeUrl).toString());
        GetMethod tGet = new GetMethod(tEncodeUrl);
        tGet.addRequestHeader("Content-Type",
                new StringBuilder().append(tType).append(";charset=").append(tEncode).toString());

        if (StringUtils.isNotEmpty(cAueryString)) {
            tGet.setQueryString(cAueryString);
        }
        logger.info(new StringBuilder().append("cUrl=").append(cUrl).toString());
        return get(tGet, tEncode);
    }

    public static String getRequest(String cUrl, String cAueryString) {
        return request(cUrl, "GET", null, cAueryString);
    }

    private static String get(GetMethod cGet, String cCharSet) {
        HttpClient tHttpClient = new HttpClient();
        int tStatus = 0;
        InputStream tInputStream = null;
        try {
            tStatus = tHttpClient.executeMethod(cGet);
            tInputStream = cGet.getResponseBodyAsStream();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        logger.info(new StringBuilder().append("tStatus:").append(tStatus).toString());
        return getResponse(tStatus, tInputStream, cCharSet);
    }

    private static String getResponse(int cStatus, InputStream cInputStream, String cCharSet) {
        String tEncode = (StringUtils.isNotEmpty(cCharSet)) ? cCharSet : "UTF-8";
        StringBuilder tBuilder = new StringBuilder();
        if ((cStatus == 200) && (cInputStream != null)) {
            try {
                BufferedReader tReader = new BufferedReader(new InputStreamReader(cInputStream, tEncode));
                String tLine = null;
                while ((tLine = tReader.readLine()) != null)
                    tBuilder.append(tLine);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } finally {
                try {
                    cInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tBuilder.toString();
    }

    private static String request(String cUrl, String cMethod, List<NameValuePair> cList, String cBody) {
        StringBuilder tBuilder = new StringBuilder();
        if (StringUtils.isEmpty(cUrl)) {
            return "";
        }
        HttpClient tHttpClient = new HttpClient();
        int tStatus = 0;
        InputStream tInputStream = null;
        if (cMethod.equalsIgnoreCase("get")) {
            String tEncodeUrl = cUrl;
            try {
                tEncodeUrl = URIUtil.encodeQuery(cUrl, "UTF-8");
            } catch (URIException e) {
                e.printStackTrace();
            }
            GetMethod tGet = new GetMethod(tEncodeUrl);
            tGet.addRequestHeader("Content-Type", "text/tHtml;charset=UTF-8");
            if ((cList != null) && (!(cList.isEmpty()))) {
                NameValuePair[] tArr = new NameValuePair[cList.size()];
                tGet.setQueryString((NameValuePair[]) cList.toArray(tArr));
            } else if (StringUtils.isNotEmpty(cBody)) {
                tGet.setQueryString(cBody);
            }
            try {
                logger.info(new StringBuilder().append("tGet = ").append(tGet).toString());
                tStatus = httpClient.executeMethod(tGet);
                tInputStream = tGet.getResponseBodyAsStream();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        } else if (cMethod.equalsIgnoreCase("tPost")) {
            PostMethod tPost = new PostMethod(cUrl);
            tPost.addRequestHeader("Content-Type", "text/tHtml;charset=UTF-8");
            if ((cList != null) && (!(cList.isEmpty()))) {
                NameValuePair[] tArr = new NameValuePair[cList.size()];
                tPost.setRequestBody((NameValuePair[]) cList.toArray(tArr));
            } else if (StringUtils.isNotEmpty(cBody)) {
                tPost.setRequestBody(cBody);
            }
            try {
                tStatus = tHttpClient.executeMethod(tPost);
                tInputStream = tPost.getResponseBodyAsStream();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }
        }
        if ((tStatus == 200) && (tInputStream != null)) {
            try {
                BufferedReader tReader = new BufferedReader(new InputStreamReader(tInputStream, "UTF-8"));
                String tLine = null;
                while ((tLine = tReader.readLine()) != null)
                    tBuilder.append(tLine);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    tInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tBuilder.toString();
    }

    static {
        //Protocol tMyhttps = new Protocol("https", new HttpsSecureProtocolSocketFactory(), 443);
        //Protocol.registerProtocol("https", tMyhttps);
        httpClient = new HttpClient();
    }
}
