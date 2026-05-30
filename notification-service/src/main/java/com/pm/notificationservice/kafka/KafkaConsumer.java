package com.pm.notificationservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final NotificationService notificationService;

    public KafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "patient" , groupId = "notification-service")
    public void consumeEvent(byte[] event){
        try{
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            log.info("Kafka Event received: topic: {} , event: {}" , "patient",patientEvent);
            notificationService.handleKafkaEvent(patientEvent);
        } catch (InvalidProtocolBufferException e){
            log.error("Error deserialising event {}",e.getMessage());
        }
    }

}
