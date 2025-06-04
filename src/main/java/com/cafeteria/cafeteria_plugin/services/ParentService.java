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

/**
 * Zentraler Service für die Elternverwaltung im Schulverwaltungssystem.
 *
 * Diese Klasse ist verantwortlich für:
 * - Verwaltung von Elternkonten und deren Daten
 * - Zuordnung von Schülern zu Eltern
 * - Abruf von Elterninformationen nach verschiedenen Kriterien
 * - Verwaltung von E-Mail-Adressen für Kommunikation
 *
 * Der Service stellt sicher, dass alle Elterndaten korrekt verwaltet
 * und die Beziehungen zu Schülern ordnungsgemäß gepflegt werden.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Parent
 * @see Student
 * @since 2025-01-01
 */
@Service
public class ParentService {

    /**
     * Repository für Elternoperationen.
     */
    @Autowired
    private ParentRepository parentRepository;

    /**
     * Repository für Schüleroperationen.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Repository für Passwort-Reset-Token.
     */
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    /**
     * Repository für Benutzeroperationen.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Alle Eltern abrufen.
     *
     * @return Liste aller Eltern im System
     */
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    /**
     * Elternteil nach ID abrufen.
     *
     * @param id Die ID des gesuchten Elternteils
     * @return Das gefundene Elternteil
     * @throws IllegalArgumentException Falls Elternteil nicht gefunden wird
     */
    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil mit der ID " + id + " wurde nicht gefunden."));
    }

    /**
     * Elternteil zur Datenbank hinzufügen.
     *
     * @param parent Das zu speichernde Elternteil
     * @return Das gespeicherte Elternteil mit generierter ID
     */
    @Transactional
    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    /**
     * Elternteil aus der Datenbank löschen (inkl. zugehörige Tokens und User-Daten).
     *
     * Diese Methode führt eine vollständige Löschung durch, einschließlich
     * aller zugehörigen Tokens und Benutzerdaten.
     *
     * @param id Die ID des zu löschenden Elternteils
     * @throws IllegalArgumentException Falls Elternteil nicht gefunden wird
     */
    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil mit der ID " + id + " wurde nicht gefunden."));

        tokenRepository.deleteAllByUser_Id(parent.getId());
        parentRepository.delete(parent);
        userRepository.deleteById(parent.getId());
    }

    /**
     * Elternteil aktualisieren.
     *
     * Diese Methode aktualisiert alle relevanten Felder des Elternteils,
     * einschließlich Namen, E-Mail-Adressen, Telefonnummern und Profilbild.
     *
     * @param id Die ID des zu aktualisierenden Elternteils
     * @param updatedParent Das Elternteil mit den neuen Daten
     * @return Das aktualisierte Elternteil
     * @throws IllegalArgumentException Falls Elternteil nicht gefunden wird
     */
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

    /**
     * Schüler einem Elternteil zuordnen.
     *
     * Diese Methode erstellt eine Beziehung zwischen einem Schüler und
     * seinem Elternteil.
     *
     * @param parentId Die ID des Elternteils
     * @param student Der zuzuordnende Schüler
     * @return Der gespeicherte Schüler mit der neuen Eltern-Zuordnung
     * @throws IllegalArgumentException Falls Elternteil nicht existiert
     */
    @Transactional
    public Student addStudentToParent(Long parentId, Student student) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil existiert nicht."));

        student.setParent(parent);
        return studentRepository.save(student);
    }

    /**
     * E-Mail-Adressen der Eltern einer bestimmten Klasse abrufen.
     *
     * Diese Methode sammelt alle E-Mail-Adressen (sowohl Mutter als auch Vater)
     * der Eltern, deren Kinder in der angegebenen Klasse sind.
     *
     * @param classId Die ID der Klasse
     * @return Liste aller gültigen E-Mail-Adressen der Eltern
     */
    public List<String> getParentEmailsByClassId(Long classId) {
        List<Parent> parents = parentRepository.findDistinctByStudents_StudentClass_Id(classId);

        return parents.stream()
                .flatMap(parent -> Stream.of(parent.getMotherEmail(), parent.getFatherEmail()))
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Elternteil nach Benutzername finden.
     *
     * @param username Der Benutzername des gesuchten Elternteils
     * @return Das gefundene Elternteil oder null falls nicht gefunden
     */
    public Parent findByUsername(String username) {
        return parentRepository.findByUsername(username);
    }

    /**
     * Elternteil speichern (neu oder aktualisiert).
     *
     * @param parent Das zu speichernde Elternteil
     * @return Das gespeicherte Elternteil
     */
    @Transactional
    public Parent saveParent(Parent parent) {
        return parentRepository.save(parent);
    }
}