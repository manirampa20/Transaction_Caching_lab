package com.entitymappinglab.entitymapping.services;

import com.entitymappinglab.entitymapping.modules.Doctor;
import com.entitymappinglab.entitymapping.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, PlatformTransactionManager transactionManager) {
        this.doctorRepository = doctorRepository;
        this.transactionManager = transactionManager;
    }

    // Declarative transaction for creating a Doctor
    @Transactional
    @CachePut(value = "doctors", key = "#doctor.emNo")
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // Caching a specific Doctor by ID
    @Cacheable(value = "doctors", key = "#id")
    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    // Caching all Doctors
    @Cacheable(value = "doctors")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, isolation = Isolation.SERIALIZABLE)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Update a Doctor with specific cache eviction
    @Transactional
    @CachePut(value = "doctors", key = "#id")
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor doctor = getDoctorById(id);
        doctor.setSpecialty(updatedDoctor.getSpecialty());
        // Update other fields as necessary
        return doctorRepository.save(doctor);
    }

    // Delete a Doctor and evict cache
    @Transactional
    @CacheEvict(value = "doctors", key = "#id")
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }


}
