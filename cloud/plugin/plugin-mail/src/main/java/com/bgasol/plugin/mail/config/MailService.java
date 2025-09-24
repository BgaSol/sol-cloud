package com.bgasol.plugin.mail.config;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    /**
     * 发送带 HTML 内容和内联资源的邮件
     */
    public boolean sendHtmlMail(String to, String subject, String htmlContent, List<String> fileIds) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = createHelper(mimeMessage, to, subject, htmlContent);


            javaMailSender.send(mimeMessage);
            log.info("发送邮件成功, 收件人: {}", to);
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return false;
        }
    }

    /**
     * 构建 MimeMessageHelper
     */
    private MimeMessageHelper createHelper(MimeMessage mimeMessage, String to, String subject, String htmlContent)
            throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(mailUsername);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        return helper;
    }

}
