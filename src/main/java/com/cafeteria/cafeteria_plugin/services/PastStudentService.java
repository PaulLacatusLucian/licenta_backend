package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import com.cafeteria.cafeteria_plugin.repositories.PastStudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PastStudentService {

    private final PastStudentRepository pastStudentRepository;

    public PastStudentService(PastStudentRepository pastStudentRepository) {
        this.pastStudentRepository = pastStudentRepository;
    }

    public List<PastStudent> getAllPastStudents() {
        return pastStudentRepository.findAll();
    }

    public PastStudent getPastStudentById(Long id) {
        return pastStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PastStudent not found with id: " + id));
    }

    public PastStudent savePastStudent(PastStudent pastStudent) {
        return pastStudentRepository.save(pastStudent);
    }

    public void deletePastStudent(Long id) {
        pastStudentRepository.deleteById(id);
    }
}
