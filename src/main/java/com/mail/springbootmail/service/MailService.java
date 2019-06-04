package com.mail.springbootmail.service;

import com.mail.springbootmail.dto.MailDto;
import com.mail.springbootmail.response.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author   Mr丶s
 * @date   2019/5/7 16:35
 * @description
 */
public interface MailService {


    /**
     * 发送简单文本的邮件
     * @param mailDto
     * @return
     */
    Response send(MailDto mailDto);

    /**
     * 发送 html 的邮件
     * @param mailDto
     * @return
     */
    Response sendWithHtml(MailDto mailDto);

    /**
     * 发送带有图片的 html 的邮件
     * @param to
     * @param subject
     * @param html
     * @param cids
     * @param filePaths
     * @return
     */
//    boolean sendWithImageHtml(String to, String cc, String bcc, String subject, String html, String[] cids, String[] filePaths);


    /**
     * 发送带有附件的邮件
     * @param mailDto
     * @param attachmentFile
     * @return
     */
    Response sendWithWithEnclosure(MailDto mailDto, MultipartFile attachmentFile);

}
