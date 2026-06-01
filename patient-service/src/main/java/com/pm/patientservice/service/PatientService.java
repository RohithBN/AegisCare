package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer){
        this.patientRepository=patientRepository;
        this.billingServiceGrpcClient=billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    //get patients
    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients=patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    //create a new patient
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient already exists with this email" + patientRequestDTO.getEmail());
        }
        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        kafkaProducer.sendEvent(patient);
        billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(),patient.getEmail());
        return PatientMapper.toDTO(patient);
    }

    //update a patients data
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with Id: "+ id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("A patient already exists with this email" + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    //delete a patient
    public void deletePatient(UUID id){
        Patient patient= patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with Id: "+id));
        patientRepository.deleteById(id);
    }

    // ensureExists: return the patient or throw
    public Patient ensureExists(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with Id: " + patientId));
    }

    // markPendingIdProof: store pending id proof key and content type
    public void markPendingIdProof(UUID patientId, String key, String contentType) {
        Patient patient = ensureExists(patientId);
        patient.setIdProofKey(key);
        patient.setIdProofContentType(contentType);
        patient.setIdProofUploadedAt(null);
        patient.setIdProofStatus("PENDING");
        patientRepository.save(patient);
    }

    // confirmIdProofUploaded: verify the key matches and store uploaded timestamp/status
    public void confirmIdProofUploaded(UUID patientId, String key) {
        Patient patient = ensureExists(patientId);
        String currentKey = patient.getIdProofKey();
        if (currentKey == null || !currentKey.equals(key)) {
            throw new IllegalArgumentException("Provided key does not match pending id-proof key for patient " + patientId);
        }
        patient.setIdProofUploadedAt(Instant.now());
        patient.setIdProofStatus("VERIFIED");
        patientRepository.save(patient);
    }
}
