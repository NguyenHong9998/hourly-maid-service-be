package com.example.hourlymaids.util;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.SendMailDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Component
public class SendMailUtils {
    /**
     * Log error endpoint.
     */
    private final static Logger logger = LoggerFactory.getLogger(SendMailUtils.class.getName());

    /**
     * Injection spring java mail.
     */
    @Autowired
    public JavaMailSender mailSender;

    /**
     * Injection HTML template.
     */
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String mailFrom;

    /**
     * Send email to user.
     *
     * @param mail
     * @return
     */
    public boolean sendMail(SendMailDomain mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(
                    Arrays.copyOf(mail.getToEmail().toArray(), mail.getToEmail().toArray().length, String[].class));
            message.setSubject(mail.getSubject());
            message.setText(mail.getMessageContent());
            if (StringUtils.isValidString(mail.getCc())) {
                message.setCc(mail.getCc());
            }
            if (StringUtils.isValidString(mail.getBcc())) {
                message.setBcc(mail.getBcc());
            }
            mailSender.send(message);
        } catch (MailException ex) {
            logger.error(StringUtils.buildLog(ex.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            throw new CustomException(Error.CANT_SEND_EMAIL.getMessage(), Error.CANT_SEND_EMAIL.getCode(),
                    HttpStatus.BAD_REQUEST);
        }

        return true;
    }

    /**
     * Send email based on template.
     *
     * @param mail
     * @param templateFile
     * @param paramsInfo
     * @return
     */
    @Async
    public boolean sendMailWithTemplate(SendMailDomain mail, String templateFile, Map<String, Object> paramsInfo) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(paramsInfo);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mailFrom);
            helper.setTo(
                    Arrays.copyOf(mail.getToEmail().toArray(), mail.getToEmail().toArray().length, String[].class));
            if (StringUtils.isValidString(mail.getCc())) {
                helper.setCc(mail.getCc());
            }
            if (StringUtils.isValidString(mail.getBcc())) {
                helper.setBcc(mail.getBcc());
            }
            String html = templateEngine.process(templateFile, context);
            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception ex) {
            logger.error(StringUtils.buildLog(ex.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getLineNumber()));
            throw new CustomException(Error.CANT_SEND_EMAIL.getMessage(), Error.CANT_SEND_EMAIL.getCode(),
                    HttpStatus.BAD_REQUEST);
        }
        return true;
    }
}
