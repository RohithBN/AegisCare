package com.pm.notificationservice.service;

import patient.events.PatientEvent;

public interface NotificationService {
    void handleKafkaEvent(PatientEvent event);

}
