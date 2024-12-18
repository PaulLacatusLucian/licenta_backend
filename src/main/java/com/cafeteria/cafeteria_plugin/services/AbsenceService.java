package com.cafeteria.cafeteria_plugin.services;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.repositories.AbsenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AbsenceService {

    private final AbsenceRepository absenceRepository;

    public AbsenceService(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    public Absence addAbsence(Absence absence) {
        return absenceRepository.save(absence);
    }

    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    public Absence updateAbsence(Long id, Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(existingAbsence -> {
                    existingAbsence.setCount(updatedAbsence.getCount());
                    return absenceRepository.save(existingAbsence);
                }).orElseThrow(() -> new IllegalArgumentException("Absence not found"));
    }

    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }
}
