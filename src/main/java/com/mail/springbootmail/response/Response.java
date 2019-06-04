package com.mail.springbootmail.response;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author   Mr丶s
 * @date   2019/6/4 16:52
 * @description   后台返回格式
 */
@Data
@NoArgsConstructor
@ToString
public class Response<T> {
    /**
     * 返回编码 0:成功  -1：失败
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 通用请求成功
     *
     * @return
     */
    public static Response success() {
        Response response = new Response();
        response.setCode(ResponseEnum.RESULT_SUCCESS.getCode());
        response.setMessage(ResponseEnum.RESULT_SUCCESS.getMessage());
        return response;
    }

    /**
     * 通用请求成功
     *
     * @param t 数据对象
     * @return
     */
    public static Response success(Object t) {
        Response response = success();
        response.setData(t);
        return response;
    }


    /**
     * 通用请求失败方法
     *
     * @param responseEnum 响应枚举类
     * @return
     */
    public static Response fail(ResponseEnum responseEnum) {
        Response response = new Response();
        response.setCode(responseEnum.getCode());
        response.setMessage(responseEnum.getMessage());
        return response;
    }

    /**
     * 通用请求失败方法
     *
     * @param code    错误码
     * @param message 提示信息
     * @return
     */
    public static Response fail(int code, String message) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 通用请求失败方法
     *
     * @param responseEnum 错误类型
     * @param data
     * @return
     */
    public static Response fail(ResponseEnum responseEnum, Object data) {
        Response response = fail(responseEnum);
        response.setData(data);
        return response;
    }

    /**
     * 响应json数据
     *
     * @return
     */
    public static void responseJson(ServletResponse response, ResponseEnum responseEnum) throws IOException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = res.getWriter();
        writer.write(JSONObject.toJSONString(fail(responseEnum)));
        writer.close();
    }

    public static String toSuccJson(ResponseEnum responseEnum) {
        return JSON.toJSONString(success(responseEnum));
    }

    public static String toFailJson(ResponseEnum responseEnum) {
        return JSON.toJSONString(fail(responseEnum));
    }
}