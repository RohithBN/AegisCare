package com.pm.patientservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // S3 object key for ID proof (nullable until uploaded)
    private String idProofKey;

    public String getIdProofBucket() {
        return idProofBucket;
    }

    public void setIdProofBucket(String idProofBucket) {
        this.idProofBucket = idProofBucket;
    }

    public String getIdProofKey() {
        return idProofKey;
    }

    public void setIdProofKey(String idProofKey) {
        this.idProofKey = idProofKey;
    }

    public Instant getIdProofUploadedAt() {
        return idProofUploadedAt;
    }

    public void setIdProofUploadedAt(Instant idProofUploadedAt) {
        this.idProofUploadedAt = idProofUploadedAt;
    }

    public String getIdProofContentType() {
        return idProofContentType;
    }

    public void setIdProofContentType(String idProofContentType) {
        this.idProofContentType = idProofContentType;
    }

    public String getIdProofStatus() {
        return idProofStatus;
    }

    public void setIdProofStatus(String idProofStatus) {
        this.idProofStatus = idProofStatus;
    }

    // optional S3 bucket and metadata for ID proof
    private String idProofBucket;
    private Instant idProofUploadedAt;
    private String idProofContentType;
    // status: NONE / PENDING / VERIFIED
    private String idProofStatus;

    @NotNull
    private String name;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private LocalDate registeredDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @Email String email) {
        this.email = email;
    }

    public @NotNull String getAddress() {
        return address;
    }

    public void setAddress(@NotNull String address) {
        this.address = address;
    }

    public @NotNull LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotNull LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotNull LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(@NotNull LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

}