package com.duoc.backend.care;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CareService {

    private final CareRepository careRepository;

    public CareService(CareRepository careRepository) {
        this.careRepository = careRepository;
    }

    public List<Care> getAllCares() {
        return (List<Care>) careRepository.findAll();
    }

    public Care getCareById(Long id) {
        return careRepository.findById(id).orElse(null);
    }

    public Care saveCare(Care care) {
        return careRepository.save(care);
    }

    public void deleteCare(Long id) {
        careRepository.deleteById(id);
    }
}