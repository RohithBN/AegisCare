package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.PresignedPutResponse;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import com.pm.patientservice.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name= "Patient" , description = "API Endpoints for patient service")
public class PatientController {
    private final PatientService patientService;
    private final S3Service s3Service;

    public PatientController(PatientService patientService, S3Service s3Service) {
        this.patientService = patientService;
        this.s3Service = s3Service;
    }

    @GetMapping
    @Operation(summary = "Get Patients")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patients = patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }


    @PostMapping
    @Operation(summary = "Create Patient")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @Validated({Default.class , CreatePatientValidationGroup.class})
            @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patient = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(patient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Patient")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @Validated({Default.class})
            @RequestBody PatientRequestDTO patientRequestDTO,@PathVariable UUID id) {
        PatientResponseDTO patient = patientService.updatePatient(id,patientRequestDTO);
        return ResponseEntity.ok().body(patient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/id-proof/presign")
    public ResponseEntity<?> presign(@PathVariable UUID id , @RequestBody PresignRequest req){
        // validate patient exists
        patientService.ensureExists(id);

        // validate file type/size (basic)
        if (req.fileSize > 10 * 1024 * 1024L) { // 10 MB max sample
            return ResponseEntity.badRequest().body("File too large");
        }
        String key = s3Service.generateKeyForPatientId(id.toString(), req.fileName);
        PresignedPutResponse resp = s3Service.getPresignedPutUrl(key, req.contentType, Duration.ofMinutes(5), Long.toString(req.fileSize) );

        // Optionally save a pending DB record linking this key to patient (status=PENDING)
        patientService.markPendingIdProof(id, key, req.contentType);

        return ResponseEntity.ok(Map.of(
                "url", resp.url,
                "key", key,
                "headers", resp.headers
        ));
        }

    @PostMapping("/{id}/id-proof/confirm")
    public ResponseEntity<?> confirm(@PathVariable UUID id, @RequestParam String key) {
        if (!s3Service.exists(key)) {
            return ResponseEntity.badRequest().body("Object not found in S3");
        }
        patientService.confirmIdProofUploaded(id, key);
        return ResponseEntity.ok().build();
    }

    public static class PresignRequest {
        public String fileName;
        public String contentType;
        public long fileSize;
    }

}
