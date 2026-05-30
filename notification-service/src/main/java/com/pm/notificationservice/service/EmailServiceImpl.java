package com.pm.notificationservice.service;

import com.pm.notificationservice.model.EmailDetails;
import com.pm.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private  final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender javaMailSender;
    private final String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            @Value("${spring.mail.username}") String sender) {
        this.javaMailSender = javaMailSender;
        this.sender = sender;
    }

    @Override
    public void sendPlainTextMail(EmailDetails emailDetails){
        try {

            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMsgBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(mailMessage);
            log.debug("Mail sent successfully to {}", emailDetails.getRecipient());

        } catch (Exception e) {
            log.error("Error sending mail to {}: {}", emailDetails.getRecipient(), e.getMessage());
            throw new RuntimeException("Failed to send email to " + emailDetails.getRecipient(), e);
        }
    }

    @Override
    public void sendHTMLMail(EmailDetails emailDetails){

    }
}
