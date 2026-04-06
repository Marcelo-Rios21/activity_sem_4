package com.duoc.backend.medication;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationService(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    public List<Medication> getAllMedications() {
        return (List<Medication>) medicationRepository.findAll();
    }

    public Medication getMedicationById(Long id) {
        return medicationRepository.findById(id).orElse(null);
    }

    public Medication saveMedication(Medication medication) {
        return medicationRepository.save(medication);
    }

    public void deleteMedication(Long id) {
        medicationRepository.deleteById(id);
    }
}