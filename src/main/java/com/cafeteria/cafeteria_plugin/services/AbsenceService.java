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

    // Adăugarea unei absențe
    public Absence addAbsence(Absence absence) {
        return absenceRepository.save(absence);
    }

    // Obținerea tuturor absențelor
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    // Obținerea unei absențe după ID
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    // Actualizarea unei absențe
    public Absence updateAbsence(Long id, Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(existingAbsence -> {
                    existingAbsence.setSubject(updatedAbsence.getSubject());
                    existingAbsence.setDate(updatedAbsence.getDate());
                    existingAbsence.setClassSession(updatedAbsence.getClassSession());
                    return absenceRepository.save(existingAbsence);
                }).orElseThrow(() -> new IllegalArgumentException("Absence not found"));
    }

    // Ștergerea unei absențe
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }

    // Calcularea numărului total de absențe pentru un student
    public int getTotalAbsencesForStudent(Long studentId) {
        return absenceRepository.countByStudentId(studentId);
    }

    public Absence saveAbsence(Absence absence) {
        // Validare: verifică dacă studentul, sesiunea și data sunt prezente
        if (absence.getStudent() == null || absence.getClassSession() == null || absence.getDate() == null) {
            throw new IllegalArgumentException("Student, Class Session, and Date are required fields.");
        }

        // Validare suplimentară: verifică dacă absența pentru acest student la această sesiune și dată există deja
        boolean exists = absenceRepository.existsByStudentIdAndClassSessionIdAndDate(
                absence.getStudent().getId(),
                absence.getClassSession().getId(),
                absence.getDate()
        );

        if (exists) {
            throw new IllegalArgumentException("Absence already exists for this student in this class session on this date.");
        }

        // Salvează absența dacă totul este valid
        return absenceRepository.save(absence);
    }

}
