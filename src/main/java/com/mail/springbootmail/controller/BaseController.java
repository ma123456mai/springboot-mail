package com.mail.springbootmail.controller;


import com.mail.springbootmail.response.Response;
import com.mail.springbootmail.response.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author   Mr丶s
 * @date   2019/5/6 19:07
 * @description
 */
@Slf4j
public class BaseController {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception ex) {
        log.error("请求异常>>>>>>>>>>>>>>", ex);
        return Response.fail( ResponseEnum.RESULT_ERROR.getCode(),ex.getMessage());
    }


}
