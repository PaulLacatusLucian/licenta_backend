package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.*;
import com.cafeteria.cafeteria_plugin.email.EmailService;
import com.cafeteria.cafeteria_plugin.mappers.*;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.*;
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
 * REST-Controller für alle elternbezogenen Operationen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für die Verwaltung von Eltern bereit
 * und ermöglicht sowohl administrative Operationen als auch elternspezifische
 * Self-Service-Funktionen.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Eltern (Admin)
 * - Self-Service für angemeldete Eltern
 * - Einsicht in Kinder-Daten (Noten, Abwesenheiten, Stundenplan)
 * - Cafeteria-Bestellungen für Kinder
 * - Kommunikation mit Lehrern
 * - Profilbild-Upload
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle
 * - JWT-Token-Validierung
 * - Datenfilterung nach Eltern-Kind-Beziehung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see ParentService
 * @see Parent
 * @see ParentDTO
 * @since 2024-12-18
 */
@RestController
@RequestMapping("/parents")
public class ParentController {

    /**
     * Verzeichnis für Datei-Uploads aus der Konfiguration.
     */
    @Value("${image.upload.dir}")
    private String uploadDir;

    @Autowired
    private ParentService parentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AbsenceService absenceService;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private OrderHistoryMapper orderHistoryMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AbsenceMapper absenceMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Ruft alle Eltern im System ab.
     * <p>
     * Nur Administratoren haben Zugriff auf die vollständige Elternliste.
     *
     * @return ResponseEntity mit Liste aller Eltern als DTOs
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        List<ParentDTO> parentDTOs = parentService.getAllParents()
                .stream()
                .map(parentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(parentDTOs);
    }

    /**
     * Ruft einen spezifischen Elternteil anhand seiner ID ab.
     * <p>
     * Zugänglich für den Elternteil selbst und Administratoren.
     *
     * @param id Eindeutige ID des Elternteils
     * @return ResponseEntity mit den Elterndaten als DTO
     */
    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        Parent parent = parentService.getParentById(id);
        return ResponseEntity.ok(parentMapper.toDto(parent));
    }

    /**
     * Aktualisiert die Daten eines existierenden Elternteils.
     * <p>
     * Zugänglich für den Elternteil selbst und Administratoren.
     *
     * @param id        ID des zu aktualisierenden Elternteils
     * @param parentDTO Eltern-DTO mit neuen Daten
     * @return ResponseEntity mit dem aktualisierten Elternteil als DTO
     */
    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ParentDTO> updateParent(@PathVariable Long id, @RequestBody ParentDTO parentDTO) {
        Parent updatedParent = parentService.updateParent(id, parentMapper.toEntity(parentDTO));
        return ResponseEntity.ok(parentMapper.toDto(updatedParent));
    }

    /**
     * Löscht einen Elternteil vollständig aus dem System.
     * <p>
     * Nur Administratoren können Eltern löschen.
     * Führt eine sichere Löschung mit Bereinigung aller Referenzen durch.
     *
     * @param id ID des zu löschenden Elternteils
     * @return ResponseEntity mit No-Content-Status bei Erfolg
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ruft die Daten des dem angemeldeten Elternteil zugeordneten Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht der Daten ihres Kindes.
     * Zugänglich auch für Lehrer und Administratoren mit entsprechenden Tokens.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit den Kinderdaten als DTO oder Not-Found
     */
    @PreAuthorize("hasRole('PARENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    @GetMapping("/me/child")
    public ResponseEntity<?> getStudentForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> ResponseEntity.ok(studentMapper.toDTO(student)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StudentDTO()));
    }

    /**
     * Fügt einen Schüler zu einem Eltern-Account hinzu.
     * <p>
     * Nur Administratoren können diese Zuordnung vornehmen.
     * Wird typischerweise bei der Registrierung neuer Schüler verwendet.
     *
     * @param token   JWT-Authorization-Header
     * @param student Schüler-Objekt, das hinzugefügt werden soll
     * @return ResponseEntity mit dem hinzugefügten Schüler oder Fehlermeldung
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/me/add-student")
    public ResponseEntity<?> addStudentToParent(@RequestHeader("Authorization") String token, @RequestBody Student student) {
        try {
            String jwt = token.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(jwt);
            Parent parent = parentService.findByUsername(username);

            Student added = parentService.addStudentToParent(parent.getId(), student);
            return ResponseEntity.ok(added);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Ruft die Gesamtanzahl der Abwesenheiten des Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht der Abwesenheitsstatistik ihres Kindes.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Map containing "total" als Schlüssel
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/absences")
    public ResponseEntity<?> getChildAbsencesForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    int total = absenceService.getTotalAbsencesForStudent(student.getId());
                    return ResponseEntity.ok(Map.of("total", total));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("total", 0)));
    }

    /**
     * Ruft alle Noten des Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht der Noten ihres Kindes.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Noten als DTOs
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/grades")
    public ResponseEntity<?> getChildGradesForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    List<GradeDTO> grades = gradeService.getGradesByStudent(student.getId());
                    return ResponseEntity.ok(grades);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()));
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/detailed-absences")
    public ResponseEntity<List<AbsenceDTO>> getDetailedChildAbsencesForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    List<Absence> absences = absenceService.getAbsencesForStudent(student.getId());
                    List<AbsenceDTO> dtoList = absences.stream().map(absenceMapper::toDto).collect(Collectors.toList());
                    return ResponseEntity.ok(dtoList);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ruft die Bestellhistorie des Kindes für einen bestimmten Zeitraum ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht der Cafeteria-Bestellungen ihres Kindes.
     *
     * @param token JWT-Authorization-Header
     * @param month Monat für den Bericht (1-12)
     * @param year  Jahr für den Bericht
     * @return ResponseEntity mit Liste der Bestellungen als DTOs
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/orders")
    public ResponseEntity<?> getChildOrdersForParent(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {

        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    var orders = menuItemService.getOrderHistoryForStudent(student.getId(), month, year)
                            .stream()
                            .map(orderHistoryMapper::toDto)
                            .toList();
                    return ResponseEntity.ok(orders);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()));
    }

    /**
     * Ruft die Gesamtanzahl der Abwesenheiten aller Kinder ab.
     * <p>
     * Self-Service-Endpunkt für Eltern mit mehreren Kindern.
     * Summiert die Abwesenheiten aller zugeordneten Kinder.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Map containing "total" als Schlüssel
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/child/total-absences")
    public ResponseEntity<Map<String, Integer>> getTotalAbsencesForChildren(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);
        if (parent == null) {
            return ResponseEntity.notFound().build();
        }

        int total = parent.getStudents().stream()
                .mapToInt(student -> absenceService.getTotalAbsencesForStudent(student.getId()))
                .sum();

        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Ruft alle Lehrer des Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht der Lehrer ihres Kindes.
     * Analysiert den Stundenplan und sammelt alle unterrichtenden Lehrer.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der Lehrer als Brief-DTOs
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/teachers")
    public ResponseEntity<List<TeacherBriefDTO>> getTeachersForMyChild(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    List<Teacher> teachers = student.getStudentClass().getSchedules().stream()
                            .map(Schedule::getTeacher)
                            .distinct()
                            .toList();

                    List<TeacherBriefDTO> result = teachers.stream().map(teacher -> {
                        TeacherBriefDTO dto = new TeacherBriefDTO();
                        dto.setName(teacher.getName());
                        dto.setSubject(teacher.getSubject());
                        dto.setEmail(teacher.getEmail());
                        return dto;
                    }).toList();

                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()));
    }

    /**
     * Sendet eine Nachricht an einen Lehrer.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Kommunikation mit Lehrern ihres Kindes.
     * Verwendet den Email-Service für die Zustellung.
     *
     * @param request DTO mit Lehrer-Email, Betreff und Nachrichteninhalt
     * @param token   JWT-Authorization-Header
     * @return ResponseEntity mit Erfolgs- oder Fehlerstatus
     */
    @PostMapping("/parents/me/send-message")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<Void> sendMessageToTeacher(@RequestBody EmailMessageDTO request, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);

        if (parent == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            emailService.sendMessageFromParent(
                    parent.getEmail(),
                    request.getTeacherEmail(),
                    request.getSubject(),
                    request.getContent()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Ruft das eigene Profil des angemeldeten Elternteils ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht ihrer eigenen Daten.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit den Elterndaten als DTO
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me")
    public ResponseEntity<ParentDTO> getMyProfile(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);
        if (parent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(parentMapper.toDto(parent));
    }

    /**
     * Lädt ein Profilbild für den angemeldeten Elternteil hoch.
     * <p>
     * Self-Service-Endpunkt für Eltern zum Upload ihres Profilbilds.
     * Unterstützt gängige Bildformate und speichert das Bild im konfigurierten Verzeichnis.
     *
     * @param token JWT-Authorization-Header
     * @param file  Hochzuladende Bilddatei
     * @return ResponseEntity mit der generierten Bild-URL als JSON
     */
    @PreAuthorize("hasRole('PARENT')")
    @PostMapping("/me/profile-image")
    public ResponseEntity<String> uploadParentProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("profileImage") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Keine Datei hochgeladen");
            }

            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Parent parent = parentService.findByUsername(username);

            if (parent == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Elternteil nicht gefunden");
            }

            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile);

            String imageUrl = "/images/" + fileName;
            parent.setProfileImage(imageUrl);
            parentService.saveParent(parent);

            return ResponseEntity.ok("{\"imageUrl\": \"" + imageUrl + "\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler beim Hochladen der Datei: " + e.getMessage());
        }
    }

    /**
     * Ruft die nächsten anstehenden Unterrichtsstunden des Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht des Stundenplans ihres Kindes.
     * Gibt die nächsten 3 anstehenden Stunden zurück.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Liste der nächsten Unterrichtsstunden
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/upcoming-classes")
    public ResponseEntity<List<ScheduleDTO>> getChildUpcomingClassesForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    List<Schedule> schedules = studentService.getUpcomingSchedules(student.getId());
                    List<ScheduleDTO> dtos = schedules.stream()
                            .map(scheduleMapper::toDto)
                            .toList();
                    return ResponseEntity.ok(dtos);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()));
    }

    /**
     * Ruft den vollständigen Stundenplan des Kindes ab.
     * <p>
     * Self-Service-Endpunkt für Eltern zur Einsicht des kompletten wöchentlichen
     * Stundenplans ihres Kindes mit Klasseninformationen.
     *
     * @param token JWT-Authorization-Header
     * @return ResponseEntity mit Map containing Klassenname und Stundenplan
     */
    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/me/child/class-schedule")
    public ResponseEntity<?> getChildClassScheduleForParent(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        Parent parent = parentService.findByUsername(username);

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    List<Schedule> schedules = student.getStudentClass().getSchedules();
                    List<ScheduleDTO> dtos = schedules.stream()
                            .map(scheduleMapper::toDto)
                            .toList();

                    String className = student.getStudentClass().getName();

                    return ResponseEntity.ok(
                            Map.of(
                                    "className", className,
                                    "schedule", dtos
                            )
                    );
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of()));
    }
}