package com.pm.appointmentservice.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class AppointmentRequestDTO {
    public LocalDate appointmentData;
    public LocalDate startTime;
    public LocalDate endTime;
    public String reason;
    public String doctorName;
}
