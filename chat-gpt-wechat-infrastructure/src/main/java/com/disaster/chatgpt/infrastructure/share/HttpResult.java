package com.disaster.chatgpt.infrastructure.share;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口统一信息返回类
 *
 * @author disaster
 * @version 1.0
 */
@Data
public class HttpResult<T> implements Serializable {
    private static final long serialVersionUID = 15869325700230991L;
    private int code;
    private String msg;
    private boolean status;
    private T data;

    protected HttpResult() {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = ResultCodeEnum.SUCCESS.getMessage();
        this.status = true;
    }

    protected HttpResult(T data) {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = ResultCodeEnum.SUCCESS.getMessage();
        this.status = true;
        this.data = data;
    }
    protected HttpResult(String msg) {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = msg;
        this.status = true;
        this.data = null;
    }

    protected HttpResult(int code, String msg, T data) {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = ResultCodeEnum.SUCCESS.getMessage();
        this.status = true;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HttpResult(int code, String msg, T data, boolean status) {
        this.code = ResultCodeEnum.SUCCESS.getCode();
        this.msg = ResultCodeEnum.SUCCESS.getMessage();
        this.status = true;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.status = status;
    }

    public static <T> HttpResult<T> success(T data) {
        return new HttpResult(data);
    }

    public static <T> HttpResult<T> success(String msg) {
        return new HttpResult(ResultCodeEnum.SUCCESS.getCode(),msg,null);
    }

    public static <T> HttpResult<T> success(T data, String message) {
        return new HttpResult(ResultCodeEnum.SUCCESS.getCode(), message, data);
    }

    public static <T> HttpResult<T> fail(ResultCodeEnum resultCodeEnum) {
        return new HttpResult(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), (Object) null, false);
    }

    public static <T> HttpResult<T> fail(String msg) {
        return new HttpResult(ResultCodeEnum.ERROR.getCode(), msg, (Object) null, false);
    }

    public static <T> HttpResult<T> fail(int code, String msg) {
        return new HttpResult(code, msg, (Object) null, false);
    }

    public static <T> HttpResult<T> fail(int code, String msg, T data) {
        return new HttpResult(code, msg, data, false);
    }

}
