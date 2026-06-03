package com.pm.appointmentservice.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public class AppointmentResponseDTO {
    public boolean success;
    public LocalDate appointmentData;
    public LocalTime startTime;
    public LocalTime endTime;
}
