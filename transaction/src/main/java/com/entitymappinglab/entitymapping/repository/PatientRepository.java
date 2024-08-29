package com.entitymappinglab.entitymapping.repository;

import com.entitymappinglab.entitymapping.modules.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Custom query methods if needed
}
