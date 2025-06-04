package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.ClassSessionDTO;
import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.mappers.OrderHistoryMapper;
import com.cafeteria.cafeteria_plugin.mappers.ScheduleMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.MenuItemService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import com.cafeteria.cafeteria_plugin.services.AbsenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST-Controller für alle schülerbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Schülern bereit
 * und ermöglicht sowohl administrative Operationen als auch schülerspezifische
 * Self-Service-Funktionen.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Schüler (Admin/Lehrer)
 * - Self-Service für angemeldete Schüler
 * - Abwesenheitsverwaltung und -anzeige
 * - Stundenplan- und Terminanzeige
 * - Profilbild-Upload
 * - Cafeteria-Bestellhistorie
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - JWT-Token-Validierung
 * - Datenfilterung nach Benutzerkontext
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see StudentService
 * @see Student
 * @see StudentDTO
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    /**
     * Verzeichnis für Datei-Uploads aus der Konfiguration.
     */
    @Value("${image.upload.dir}")
    private String uploadDir;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderHistoryMapper orderHistoryMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Erstellt einen neuen Schüler und ordnet ihn einer Klasse zu.
     * <p>
     * Nur Lehrer und Administratoren können neue Schüler erstellen.
     * Der Schüler wird automatisch der angegebenen Klasse zugeordnet.
     *
     * @param studentDetails Schülerdaten für die Erstellung
     * @param classId        ID der Klasse, der der Schüler zugeordnet werden soll
     * @return ResponseEntity mit dem erstellten Schüler als DTO
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PostMapping("/class/{classId}")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody Student studentDetails, @PathVariable Long classId) {
        Student savedStudent = studentService.saveStudentWithClass(studentDetails, classId);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }

    /**
     * Ruft einen spezifischen Schüler anhand seiner ID ab.
     * <p>
     * Zugänglich für Schüler (eigene Daten), Lehrer und Administratoren.
     *
     * @param id Eindeutige ID des Schülers
     * @return ResponseEntity mit den Schülerdaten als DTO
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        StudentDTO dto = studentMapper.toDTO(student);
        return ResponseEntity.ok(dto);
    }

    /**
     * Ruft alle Schüler im System ab.
     * <p>
     * Nur für Lehrer und Administratoren zugänglich.
     * Gibt eine vollständige Liste aller registrierten Schüler zurück.
     *
     * @return ResponseEntity mit Liste aller Schüler als DTOs
     */
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> studentDTOs = studentService.getAllStudents().stream()
                .map(studentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }

    /**
     * Aktualisiert die Daten eines existierenden Schülers.
     * <p>
     * Zugänglich für den Schüler selbst, Lehrer und Administratoren.
     * Ermöglicht die Änderung von Basisdaten und Klassenzuordnung.
     *
     * @param id             ID des zu aktualisierenden Schülers
     * @param updatedStudent Schüler-Objekt mit neuen Daten
     * @return ResponseEntity mit dem aktualisierten Schüler als DTO
     */
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        Student savedStudent = studentService.updateStudent(id, updatedStudent);
        StudentDTO dto = studentMapper.toDTO(savedStudent);
        return ResponseEntity.ok(dto);
    }

    /**
     * Löscht einen Schüler vollständig aus dem System.
     * <p>
     * Nur Administratoren können Schüler löschen.
     * Führt eine sichere Löschung mit Bereinigung aller Referenzen durch.
     *
     * @param id ID des zu löschenden Schülers
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft die Daten des aktuell angemeldeten Schülers ab.
     * <p>
     * Self-Service-Endpunkt für Schüler, um ihre eigenen Daten einzusehen.
     * Verwendet JWT-Token zur Identifikation des Schülers.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit den Schülerdaten als DTO
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<StudentDTO> getCurrentStudent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);

        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(studentMapper.toDTO(student));
    }

    /**
     * Ruft die Gesamtanzahl der Abwesenheiten des aktuellen Schülers ab.
     * <p>
     * Self-Service-Endpunkt für Schüler zur Einsicht ihrer Abwesenheitsstatistik.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Map containing "total" als Schlüssel
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/total-absences")
    public ResponseEntity<Map<String, Integer>> getTotalAbsencesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        int total = absenceService.getTotalAbsencesForStudent(student.getId());
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Ruft die nächsten anstehenden Unterrichtsstunden des Schülers ab.
     * <p>
     * Self-Service-Endpunkt, der eine intelligente Analyse des Stundenplans
     * durchführt und die nächsten 3 anstehenden Stunden zurückgibt.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der nächsten Unterrichtsstunden
     */
    @GetMapping("/me/upcoming-classes")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ScheduleDTO>> getUpcomingClassesForCurrentStudent(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        List<Schedule> schedules = studentService.getUpcomingSchedules(student.getId());
        List<ScheduleDTO> dtos = schedules.stream().map(scheduleMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lädt ein Profilbild für den aktuellen Schüler hoch.
     * <p>
     * Self-Service-Endpunkt für Schüler zum Upload ihres Profilbilds.
     * Unterstützt gängige Bildformate und speichert das Bild im konfigurierten Verzeichnis.
     *
     * @param token JWT-Authorization-Header
     * @param file  Hochzuladende Bilddatei
     * @return ResponseEntity mit der generierten Bild-URL als JSON
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/me/profile-image")
    public ResponseEntity<String> uploadStudentProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("profileImage") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Keine Datei hochgeladen");
            }

            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Student student = studentService.findByUsername(username);

            if (student == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Schüler nicht gefunden");
            }

            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile);

            String imageUrl = "/images/" + fileName;
            student.setProfileImage(imageUrl);
            studentService.saveStudent(student);

            return ResponseEntity.ok("{\"imageUrl\": \"" + imageUrl + "\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Hochladen der Datei: " + e.getMessage());
        }
    }

    /**
     * Ruft die Bestellhistorie des aktuellen Schülers ab.
     * <p>
     * Self-Service-Endpunkt für Schüler zur Einsicht ihrer Cafeteria-Bestellungen
     * für einen bestimmten Monat und Jahr.
     *
     * @param token JWT-Authorization-Header
     * @param month Monat für den Bericht (1-12)
     * @param year  Jahr für den Bericht
     * @return ResponseEntity mit Liste der Bestellungen als DTOs
     */
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me/orders")
    public ResponseEntity<?> getStudentOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var orders = menuItemService.getOrderHistoryForStudent(student.getId(), month, year)
                .stream()
                .map(orderHistoryMapper::toDto)
                .toList();

        return ResponseEntity.ok(orders);
    }
}