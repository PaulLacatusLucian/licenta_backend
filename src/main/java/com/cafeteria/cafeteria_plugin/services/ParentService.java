package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.email.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    // Alle Eltern abrufen
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    // Elternteil nach ID abrufen
    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil mit der ID " + id + " wurde nicht gefunden."));
    }

    // Elternteil zur Datenbank hinzufügen
    @Transactional
    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    // Elternteil aus der Datenbank löschen (inkl. zugehörige Tokens und User-Daten)
    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil mit der ID " + id + " wurde nicht gefunden."));

        tokenRepository.deleteAllByUser_Id(parent.getId());
        parentRepository.delete(parent);
        userRepository.deleteById(parent.getId());
    }

    // Elternteil aktualisieren
    @Transactional
    public Parent updateParent(Long id, Parent updatedParent) {
        Parent existingParent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil mit der ID " + id + " wurde nicht gefunden."));

        existingParent.setMotherName(updatedParent.getMotherName());
        existingParent.setMotherEmail(updatedParent.getMotherEmail());
        existingParent.setMotherPhoneNumber(updatedParent.getMotherPhoneNumber());
        existingParent.setFatherName(updatedParent.getFatherName());
        existingParent.setFatherEmail(updatedParent.getFatherEmail());
        existingParent.setFatherPhoneNumber(updatedParent.getFatherPhoneNumber());
        existingParent.setProfileImage(updatedParent.getProfileImage()); // Profilbild aktualisieren

        return parentRepository.save(existingParent);
    }

    // Schüler einem Elternteil zuordnen
    @Transactional
    public Student addStudentToParent(Long parentId, Student student) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil existiert nicht."));

        student.setParent(parent);
        return studentRepository.save(student);
    }

    // E-Mail-Adressen der Eltern einer bestimmten Klasse abrufen
    public List<String> getParentEmailsByClassId(Long classId) {
        List<Parent> parents = parentRepository.findDistinctByStudents_StudentClass_Id(classId);

        return parents.stream()
                .flatMap(parent -> Stream.of(parent.getMotherEmail(), parent.getFatherEmail()))
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    // Elternteil nach Benutzername finden
    public Parent findByUsername(String username) {
        return parentRepository.findByUsername(username);
    }

    // Elternteil speichern (neu oder aktualisiert)
    @Transactional
    public Parent saveParent(Parent parent) {
        return parentRepository.save(parent);
    }
}
