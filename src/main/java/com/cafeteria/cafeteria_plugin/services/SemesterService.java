package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Semester;
import com.cafeteria.cafeteria_plugin.repositories.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;

    @Autowired
    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public Semester getCurrentSemester() {
        return semesterRepository.findById(1L) // Presupunem că există un singur rând pentru semestru
                .orElseThrow(() -> new IllegalStateException("Semestrul nu a fost inițializat."));
    }

    public Semester incrementSemester() {
        Semester semester = getCurrentSemester();
        semester.setCurrentSemester(semester.getCurrentSemester() + 1);
        return semesterRepository.save(semester);
    }
}
