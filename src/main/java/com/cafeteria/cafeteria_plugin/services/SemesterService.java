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

    // Aktuelles Semester abrufen (wir nehmen an, dass es nur einen Datensatz gibt)
    public Semester getCurrentSemester() {
        return semesterRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("Das Semester wurde nicht initialisiert."));
    }

    // Semester erhöhen (z. B. von 1 auf 2)
    public Semester incrementSemester() {
        Semester semester = getCurrentSemester();
        semester.setCurrentSemester(semester.getCurrentSemester() + 1);
        return semesterRepository.save(semester);
    }
}
