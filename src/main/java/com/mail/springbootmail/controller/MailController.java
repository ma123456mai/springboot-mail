package com.mail.springbootmail.controller;

import com.alibaba.fastjson.JSONObject;
import com.mail.springbootmail.dto.MailDto;
import com.mail.springbootmail.response.Response;
import com.mail.springbootmail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Mr丶s
 * @date 2019/5/7 16:59
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/mailSend")
public class MailController extends BaseController {

    @Autowired
    private MailService mailService;

    /**
     * 发送普通文本邮件
     * @param mailDto
     * @return
     */
    @PostMapping("send")
    public Response send(@RequestBody MailDto mailDto) {
        return mailService.send(mailDto);
    }

    /**
     * 发送HTML邮件
     * @param mailDto
     * @return
     */
    @PostMapping("sendHtml")
    public Response sendCode(@RequestBody MailDto mailDto) {
        log.info("内容："+mailDto.getContent());
        JSONObject.toJSONString(mailDto.getContent());
        return mailService.sendWithHtml(mailDto);
    }

    /**
     * 发送带附件的邮件
     * @param mailDto
     * @param attachmentFile
     * @return
     */
    @PostMapping("sendWithWithEnclosure")
    public Response sendWithWithEnclosure(MailDto mailDto, MultipartFile attachmentFile) {
        JSONObject.toJSONString("附件发送：" + mailDto);
        return mailService.sendWithWithEnclosure(mailDto, attachmentFile);
    }


}
