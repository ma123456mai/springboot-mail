package com.mail.springbootmail.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mail.springbootmail.dto.MailAttachment;
import com.mail.springbootmail.dto.MailDto;
import com.mail.springbootmail.response.Response;
import com.mail.springbootmail.response.ResponseEnum;
import com.mail.springbootmail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Mr丶s
 * @date 2019/5/7 16:37
 * @description
 */
@Slf4j
@Service
@Configuration
public class MailServiceImpl implements MailService {

    @Value("${file.folder}")
    private String fileFolder;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 发送简单文本的邮件
     *
     * @param mailDto 收件人
     * @return
     */
    @Override
    public Response send(MailDto mailDto) {
        JavaMailSenderImpl javaMailSender = mailProtocolPatam(mailDto);

        log.info("## Ready to send mail ...");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        // 邮件发送来源
        simpleMailMessage.setFrom(mailDto.getUsername());
        simpleMailMessage.setTo(mailDto.getTo());
        if (mailDto.getCc() != null) {
            simpleMailMessage.setCc(mailDto.getCc());
        }
        if (mailDto.getBcc() != null) {
            simpleMailMessage.setBcc(mailDto.getBcc());
        }
        simpleMailMessage.setSubject(mailDto.getSubject());
        simpleMailMessage.setText(mailDto.getContent());
        JSONObject.toJSON(simpleMailMessage);
        try {
            javaMailSender.send(simpleMailMessage);
            return Response.success(ResponseEnum.RESULT_SUCCESS.getCode());
        } catch (MailException e) {
            e.printStackTrace();
        }
        return Response.fail(ResponseEnum.RESULT_MAIL_SEND_FAIL.getCode(), "邮件发送失败");

    }


    /**
     * 发送 html 的邮件
     *
     * @param mailDto 收件人
     * @return
     */
    @Override
    public Response sendWithHtml(MailDto mailDto) {
        log.info("## Ready to send mail ...");
        JavaMailSenderImpl javaMailSender = mailProtocolPatam(mailDto);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            Context context = new Context();
            context.setVariable("productName", "阿里啊啊");
            context.setVariable("code", mailDto.getVerificationCode());
            context.setVariable("time", LocalDate.now());
            String emailContent = templateEngine.process("verificationCode", context);
            mailDto.setContent(emailContent);
            //mimeMessageHelper组装
            mailParam(mimeMessageHelper, mailDto);

            javaMailSender.send(mimeMessage);
            log.info("## Send the mail with html success ...");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Send html mail error: ", e);
            return Response.fail(ResponseEnum.RESULT_MAIL_SEND_FAIL.getCode(), "邮件发送失败");
        }
        return Response.success(ResponseEnum.RESULT_SUCCESS.getCode());

    }

    /**
     * 发送带有图片的 html 的邮件
     *
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param html
     * @param cids
     * @param filePaths
     * @return
     */
//    @Override
//    public boolean sendWithImageHtml(String to, String cc, String bcc, String subject, String html, String[]
//            cids, String[] filePaths) {
//        log.info("## Ready to send mail ...");
//        JavaMailSenderImpl javaMailSender = mailInfo(mailDto);
//
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        MimeMessageHelper mimeMessageHelper = null;
//        try {
//            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//            // 邮件发送来源
//            mimeMessageHelper.setFrom(to);
//            mimeMessageHelper.setTo(to);
//            mimeMessageHelper.setCc(cc);
//            mimeMessageHelper.setBcc(bcc);
//            mimeMessageHelper.setSubject(subject);
//            mimeMessageHelper.setText(html, true);
//
//            // 设置 html 中内联的图片
//            for (int i = 0; i < cids.length; i++) {
//                FileSystemResource file = new FileSystemResource(filePaths[i]);
//                mimeMessageHelper.addInline(cids[i], file);
//            }
//
//            javaMailSender.send(mimeMessage);
//            log.info("## Send the mail with image success ...");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Send html mail error: ", e);
//            return false;
//        }
//
//        return true;
//    }


    /**
     * 发送带有附件的邮件
     *
     * @param mailDto
     * @return
     */
    @Override
    public Response sendWithWithEnclosure(MailDto mailDto, MultipartFile attachmentFile) {
        log.info("## Ready to send mail ...");
        JavaMailSenderImpl javaMailSender = mailProtocolPatam(mailDto);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            // 邮件发送来源
            //mimeMessageHelper组装
            mailParam(mimeMessageHelper, mailDto);
            // 添加附件
            File tempFile = null;
            File tempFolder = new File(fileFolder);
            if (!tempFolder.exists() || !tempFolder.isDirectory()) {
                FileUtils.forceMkdir(tempFolder);
                tempFile = new File(fileFolder, mailDto.getSubject());
                //noinspection ResultOfMethodCallIgnored
                tempFile.createNewFile();
                attachmentFile.transferTo(tempFile);
            }
            File[] attachments = null;
            attachments = new File[]{tempFile};
            // 最外层部分
            MimeMultipart wrapPart = new MimeMultipart("mixed");
            MimeMultipart htmlWithImageMultipart = new MimeMultipart("related");

            //内嵌图片部分
            addImages(attachments, htmlWithImageMultipart);
            MimeBodyPart htmlWithImageBodyPart = new MimeBodyPart();
            htmlWithImageBodyPart.setContent(htmlWithImageMultipart);
            wrapPart.addBodyPart(htmlWithImageBodyPart);
            // 附件部分
            addAttachments(attachments, wrapPart);
            mimeMessageHelper.addAttachment(Objects.requireNonNull(attachmentFile.getOriginalFilename()), attachmentFile);

            javaMailSender.send(mimeMessage);
            log.info("## Send the mail with enclosure success ...");

        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return Response.fail(ResponseEnum.RESULT_MAIL_SEND_FAIL.getCode(), "邮件发送失败");
        }
        return Response.success(ResponseEnum.RESULT_SUCCESS.getCode());

    }

    @Override
    public Response sendAttach(MailDto mailDto) {
        log.info("## Ready to send mail ...");
        log.info("附件信息,{}", mailDto.getMailAttachmentList());
        //协议参数配置
        JavaMailSenderImpl javaMailSender = mailProtocolPatam(mailDto);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            //mimeMessageHelper组装
            mailParam(mimeMessageHelper, mailDto);

            // 附件相关组装
            List<MailAttachment> mailAttachmentList = mailDto.getMailAttachmentList();
            if (!CollectionUtils.isEmpty(mailAttachmentList)) {
                getInputStream(mailAttachmentList);
                addAttachment(mimeMessageHelper, mailAttachmentList);
            }
            javaMailSender.send(mimeMessage);
            log.info("## Send the mail with enclosure success ...");
            return Response.success();

        } catch (MessagingException e) {
            e.printStackTrace();
            return Response.fail(ResponseEnum.RESULT_MAIL_SEND_FAIL.getCode(), "附件邮件发送失败>>>" + JSONObject.toJSONString(e));
        }
    }


    /**
     * 追加附件
     *
     * @param attachments
     * @param wrapPart
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private void addAttachments(File[] attachments, MimeMultipart wrapPart)
            throws MessagingException, UnsupportedEncodingException {
        if (null != attachments && attachments.length > 0) {
            for (File attachment : attachments) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(attachment));
                String fileName = dataHandler.getName();
                attachmentBodyPart.setDataHandler(dataHandler);
                // 显示指定文件名（防止文件名乱码）
                attachmentBodyPart.setFileName(MimeUtility.encodeText(fileName));
                wrapPart.addBodyPart(attachmentBodyPart);
            }
        }
    }

    /**
     * 追加内嵌图片
     *
     * @param images
     * @param multipart
     * @throws MessagingException
     */
    private void addImages(File[] images, MimeMultipart multipart) throws MessagingException {
        if (null != images && images.length > 0) {
            for (File image : images) {
                MimeBodyPart imagePart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(image));
                imagePart.setDataHandler(dataHandler);
                imagePart.setContentID(image.getName());
                multipart.addBodyPart(imagePart);
            }
        }
    }


    /**
     * @param mimeMessageHelper
     * @param mailAttachmentList
     * @Description: 多附件处理
     */
    private void addAttachment(MimeMessageHelper mimeMessageHelper, List<MailAttachment> mailAttachmentList) {
        try {
            for (MailAttachment attach : mailAttachmentList) {
                // 用流的形式发送附件邮件
                DataSource source = new ByteArrayDataSource(attach.getAttachIS(), attach.getAttachType());
                mimeMessageHelper.addAttachment(attach.getAttachName(), source);
            }
        } catch (Exception e) {
            log.error("附件邮件处理异常！{}", e);
        }
    }

    /**
     * oss下载流文件并组装邮件参数
     *
     * @param mailAttachmentList
     * @Description: 循环前置操作
     */
    private void getInputStream(List<MailAttachment> mailAttachmentList) {
        for (MailAttachment attach : mailAttachmentList) {
            log.info("附件名称,{}", attach.getAttachContent());
            attach.setAttachIS(changeInputStream(attach.getAttachContent()));
        }
    }

    /**
     * @Description: 字符串转InputStream
     * @author lc
     */
    public InputStream changeInputStream(String str) {
        InputStream is = null;
        char[] hex = null;
        byte[] data = null;
        try {
            hex = str.toCharArray();
            data = Hex.decodeHex(hex);
            is = new ByteArrayInputStream(data);
        } catch (Exception e) {
            log.error("字符串转InputStream异常！{}", e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return is;
    }

    /**
     * @Description: InputStream转16进制字符串
     * @author lc
     */
    public String changeStr(InputStream is) throws IOException {
        byte[] data = null;
        char[] hex = null;
        try {
            data = new byte[is.available()];
            is.read(data);
            is.close();
            hex = Hex.encodeHex(data);
            System.out.println("byte length:" + data.length);
            System.out.println("--------------------------");
            System.out.println("char hex length:" + hex.length);
            System.out.println("--------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String str = new String(hex);
        System.out.println("String length:" + str.length());
        return str;
    }


    /**
     * 组装邮件参数
     *
     * @param mimeMessageHelper
     * @param mailDto
     * @throws MessagingException
     */
    private void mailParam(MimeMessageHelper mimeMessageHelper, MailDto mailDto) throws MessagingException {
        //设置自定义发件人昵称
        String nick = "";
        try {
            nick = javax.mail.internet.MimeUtility.encodeText(mailDto.getAddresserName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 邮件发送来源
        mimeMessageHelper.setFrom(new InternetAddress(nick + "<" + mailDto.getUsername() + ">"));
        // 收件人
        mimeMessageHelper.setTo(getStringArray(mailDto.getTo()));
        // 主题
        mimeMessageHelper.setSubject(mailDto.getSubject());
        // 抄送人
        if (mailDto.getCc() != null) {
            mimeMessageHelper.setCc(getStringArray(mailDto.getCc()));
        }
        // 密送人
        if (mailDto.getBcc() != null) {
            mimeMessageHelper.setBcc(getStringArray(mailDto.getBcc()));
        }
        mimeMessageHelper.setText(mailDto.getContent(), true);
    }

    /**
     * 根据 , 号转换为数组
     *
     * @param str
     * @return
     */
    private String[] getStringArray(String str) {
        return str.split(",");
    }

    /**
     * 邮件参数
     *
     * @param mailDto
     */
    public JavaMailSenderImpl mailProtocolPatam(MailDto mailDto) {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        if (mailDto.getSort() == 1) {
            javaMailSenderImpl.setHost("smtp.qq.com");
            javaMailSenderImpl.setPort(465);
            javaMailSenderImpl.setUsername(mailDto.getUsername());
            javaMailSenderImpl.setPassword(mailDto.getPassword());
        } else if (mailDto.getSort() == 2) {
            javaMailSenderImpl.setHost("smtp.126.com");
            javaMailSenderImpl.setPort(465);
            javaMailSenderImpl.setUsername(mailDto.getUsername());
            javaMailSenderImpl.setPassword(mailDto.getPassword());
        } else if (mailDto.getSort() == 3) {
            javaMailSenderImpl.setHost("smtp.163.com");
            javaMailSenderImpl.setPort(994);
            javaMailSenderImpl.setUsername(mailDto.getUsername());
            javaMailSenderImpl.setPassword(mailDto.getPassword());
        }
        javaMailSenderImpl.setDefaultEncoding(String.valueOf(DEFAULT_CHARSET));
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.smtp.starttls.rejavaMailSenderuired", "true");
        p.setProperty("mail.smtp.ssl.enable", "true");
        p.setProperty("mail.default-encoding", String.valueOf(DEFAULT_CHARSET));
        javaMailSenderImpl.setJavaMailProperties(p);
        return javaMailSenderImpl;
    }


}
