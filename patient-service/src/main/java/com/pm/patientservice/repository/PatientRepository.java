package com.pm.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pm.patientservice.model.*;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient,UUID> {

    boolean existsByEmail(String email);

    // keep repository simple; higher-level id-proof operations are handled in the service layer
    boolean existsByEmailAndIdNot(String email , UUID id);
}
