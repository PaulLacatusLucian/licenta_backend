package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.AbsenceRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbsenceService {

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Neue Abwesenheit speichern
    public Absence addAbsence(Absence absence) {
        return absenceRepository.save(absence);
    }

    // Alle Abwesenheiten abrufen
    public List<Absence> getAllAbsences() {
        return absenceRepository.findAll();
    }

    // Abwesenheit nach ID abrufen
    public Optional<Absence> getAbsenceById(Long id) {
        return absenceRepository.findById(id);
    }

    // Abwesenheit aktualisieren
    public Absence updateAbsence(Long id, Absence updatedAbsence) {
        return absenceRepository.findById(id)
                .map(existingAbsence -> {
                    existingAbsence.setClassSession(updatedAbsence.getClassSession());
                    existingAbsence.setStudent(updatedAbsence.getStudent());
                    existingAbsence.setJustified(updatedAbsence.getJustified());
                    return absenceRepository.save(existingAbsence);
                }).orElseThrow(() -> new IllegalArgumentException("Abwesenheit nicht gefunden."));
    }

    // Abwesenheit löschen
    public void deleteAbsence(Long id) {
        absenceRepository.deleteById(id);
    }

    // Gesamtanzahl der Abwesenheiten eines Schülers abrufen
    public int getTotalAbsencesForStudent(Long studentId) {
        return absenceRepository.countByStudentId(studentId);
    }

    // Abwesenheit mit Validierung speichern
    public Absence saveAbsence(Absence absence) {
        if (absence.getStudent() == null || absence.getClassSession() == null) {
            throw new IllegalArgumentException("Schüler und Unterrichtseinheit sind Pflichtfelder.");
        }

        boolean exists = absenceRepository.existsByStudentIdAndClassSessionId(
                absence.getStudent().getId(),
                absence.getClassSession().getId()
        );

        if (exists) {
            throw new IllegalArgumentException("Für diesen Schüler in dieser Unterrichtseinheit existiert bereits eine Abwesenheit.");
        }

        return absenceRepository.save(absence);
    }

    // Abwesenheiten eines bestimmten Schülers abrufen
    public List<Absence> getAbsencesForStudent(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    // Prüfen, ob eine Abwesenheit für eine bestimmte Sitzung existiert
    public boolean existsByStudentIdAndClassSessionId(Long studentId, Long classSessionId) {
        return absenceRepository.existsByStudentIdAndClassSessionId(studentId, classSessionId);
    }

    // Abwesenheit als entschuldigt markieren
    public Absence justifyAbsence(Absence absence) {
        absence.setJustified(true);
        return absenceRepository.save(absence);
    }

    // Unentschuldigte Abwesenheiten für eine bestimmte Klasse abrufen
    public List<Absence> getUnjustifiedAbsencesForClass(Long classId) {
        List<Student> studentsInClass = studentRepository.findByStudentClassId(classId);

        if (studentsInClass.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> studentIds = studentsInClass.stream()
                .map(Student::getId)
                .collect(Collectors.toList());

        return absenceRepository.findUnjustifiedAbsencesByStudentIds(studentIds);
    }
}