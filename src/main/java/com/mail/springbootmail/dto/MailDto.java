package com.mail.springbootmail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author Mr丶s
 * @date 2019/5/6 16:55
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto extends ParamDto {


    /**
     *收件人，多个收件人用 {@code ;} 分隔
     */
    @NotNull
    private String to;

    /**
     * 抄送人，多个抄送人用 {@code ;} 分隔
     */
    private String cc;

    /**
     *密送人，多个密送人用 {@code ;} 分隔
     */
    private String bcc;

    /**
     *主题
     */
    @NotNull
    private String subject;

    /**
     * 内容，可引用内嵌图片，引用方式：{@code <img src="cid:内嵌图片文件名" />}
     */
    private String content;

    /**
     * html邮件
     */
    private String html;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 验证码
     */
    private String verificationCode;


}
