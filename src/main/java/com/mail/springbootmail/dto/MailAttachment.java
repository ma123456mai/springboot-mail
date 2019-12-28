package com.mail.springbootmail.dto;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @author Mr丶s
 * @date 2019/12/27 11:39
 * @description
 */
@Data
public class MailAttachment implements Serializable {

    /**
     * 附件类型
     */
    private String attachType;

    /**
     * 附件名称
     */
    private String attachName;


    /**
     * 附件内容
     */
    private String attachContent;


    /**
     * 附件流
     */
    private InputStream attachIS;
}
