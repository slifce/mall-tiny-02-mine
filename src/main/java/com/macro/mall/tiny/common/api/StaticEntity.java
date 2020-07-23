package com.macro.mall.tiny.common.api;

/**
 * Created by Administrator on 2020/7/23.
 */
/**
 * 前后端通用交互类
 *
 * @author chenyong
 */
public class StaticEntity<T> extends MessageEntity {

    private T data;

    public StaticEntity() {
        super();
    }

    public StaticEntity(int code, String mssage){
        this.setCode(code);
        this.setMessage(mssage);

    }

    public StaticEntity(int code, String mssage, T data) {
        this();
        this.setCode(code);
        this.setMessage(mssage);
        this.setData(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

