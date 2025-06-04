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

    // Alle ehemaligen Schüler abrufen
    public List<PastStudent> getAllPastStudents() {
        return pastStudentRepository.findAll();
    }

    // Ehemaligen Schüler nach ID abrufen
    public PastStudent getPastStudentById(Long id) {
        return pastStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ehemaliger Schüler mit der ID " + id + " wurde nicht gefunden."));
    }

    // Ehemaligen Schüler speichern
    public PastStudent savePastStudent(PastStudent pastStudent) {
        return pastStudentRepository.save(pastStudent);
    }

    // Ehemaligen Schüler löschen
    public void deletePastStudent(Long id) {
        pastStudentRepository.deleteById(id);
    }
}
