package com.disaster.chatgpt.infrastructure.share;

/**
 * 状态码
 *
 * @author disaster
 * @version 1.0
 */
public enum ResultCodeEnum {
    SUCCESS(200, "接口返回成功"),
    REDIRECT(301, "重定向"),
    ACCESS_DENIED(401, "未授权"),
    BAD_REQUEST(400, "请求错误"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源已变更"),
    ERROR(500, "系统内部错误"),
    ;
    private int code;

    private String message;

    ResultCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String message() {
        return this.message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
