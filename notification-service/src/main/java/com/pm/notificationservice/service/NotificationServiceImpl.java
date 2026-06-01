package com.pm.notificationservice.service;

import com.pm.notificationservice.model.EmailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    private final EmailService emailService;

    public NotificationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void handleKafkaEvent(PatientEvent event){
        log.info("Handling Kafka Event: {}", event);

        switch (event.getEventType()){
            case "PATIENT CREATED" -> {
                EmailDetails emailDetails = EmailDetails.builder()
                        .recipient(event.getEmail())
                        .subject("Welcome to our healthcare system")
                        .msgBody(String.format("Dear %s, welcome to our healthcare system! We are glad to have you on board.", event.getName()))
                        .build();

                emailService.sendPlainTextMail(emailDetails);
                log.info("Send welcome notification");
            }
            case "PATIENT_DELETED" -> {
                log.info("Send account deletion notification");
            }
        }

    }
}
