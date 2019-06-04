package com.mail.springbootmail.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Mr丶s
 * @date 2019/5/7 11:14
 * @description
 */
@Data
public class ParamDto implements Serializable {

    /**
     * 邮箱类别
     */
    private Long sort;

    /**
     * 邮箱服务器帐号
     */
    private String username;
    /**
     * 邮箱服务器授权码
     */
    private String password;

}
