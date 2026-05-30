package com.pm.notificationservice.service;

import com.pm.notificationservice.model.EmailDetails;

public interface EmailService {
    void sendPlainTextMail(EmailDetails emailDetails);

    void sendHTMLMail(EmailDetails emailDetails);
}
