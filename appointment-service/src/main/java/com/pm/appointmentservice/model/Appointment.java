package com.pm.appointmentservice.model;

import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Instant;
import java.util.UUID;

@Builder
@Entity
@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_patient_date_time", columnNames = {"patient_id", "appointment_date", "start_time"})
        })
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private String reason;

    private String doctorName;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.status = this.status == null ? AppointmentStatus.BOOKED : this.status;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

}

