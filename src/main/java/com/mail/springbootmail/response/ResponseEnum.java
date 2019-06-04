package com.mail.springbootmail.response;

/**
 * @author   Mr丶s
 * @date   2019/6/4 16:51
 * @description  业务响应
 */
public enum ResponseEnum {

    /**
     * 通用业务段
     */
    RESULT_SUCCESS(200, "成功"),
    RESULT_NOT_LOGGED_IN(401, "用户未登录"),
    RESULT_ACCESS_DENIED(402, "访问被拒绝"),
    RESULT_PERMISSION_UNAUTHORIZED(403, "权限不足"),
    RESULT_RESOURCE_NOT_FOUND(404, "目标资源不存在"),
    RESULT_ERROR(500, "服务器内部错误"),


    /**
     * 邮件相关业务  2xxx
     */
    RESULT_MAIL_SEND_FAIL(2005, "邮件发送失败"),



    ;


    private Integer code;
    private String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
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