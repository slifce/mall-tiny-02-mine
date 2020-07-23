package com.macro.mall.tiny.common.utils;

import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by Administrator on 2020/7/23.
 */
public class UTF8PostMethod extends PostMethod {

    public UTF8PostMethod(String url) {
        super(url);
    }

    @Override
    public String getRequestCharSet() {
        return "UTF-8";
    }

}
