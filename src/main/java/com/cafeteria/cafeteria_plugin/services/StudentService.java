package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.email.passwordReset.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Zentraler Service für die Schülerverwaltung im Schulverwaltungssystem.
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see Class
 * @see Schedule
 * @since 2024-12-18
 */
@Service
public class StudentService {

    /**
     * Repository für Schüleroperationen.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Repository für Klassenoperationen.
     */
    @Autowired
    private ClassRepository classRepository;

    /**
     * Repository für Unterrichtsstunden-Operationen.
     */
    @Autowired
    private ClassSessionRepository classSessionRepository;

    /**
     * Repository für Abwesenheitsoperationen.
     */
    @Autowired
    private AbsenceRepository absenceRepository;

    /**
     * Repository für ehemalige Schüler.
     */
    @Autowired
    private PastStudentRepository pastStudentRepository;

    /**
     * Repository für Notenoperationen.
     */
    @Autowired
    private GradeRepository gradeRepository;

    /**
     * Service für Klassenoperationen.
     */
    @Autowired
    private ClassService classService;

    /**
     * Repository für Benutzeroperationen.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Repository für Passwort-Reset-Token.
     */
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    /**
     * Repository für Stundenplan-Operationen.
     */
    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * Schüler in eine bestimmte Klasse speichern.
     *
     * Diese Methode ordnet einen Schüler einer bestimmten Klasse zu
     * und speichert ihn in der Datenbank.
     *
     * @param studentDetails Die Schülerdaten
     * @param classId Die ID der Klasse
     * @return Der gespeicherte Schüler mit Klassenzuordnung
     * @throws IllegalArgumentException Falls die Klasse nicht existiert
     */
    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        Class studentSchoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Die Klasse mit der ID " + classId + " existiert nicht"));

        studentDetails.setStudentClass(studentSchoolClass);
        return studentRepository.save(studentDetails);
    }

    /**
     * Schüler anhand seiner ID abrufen.
     *
     * @param id Die ID des gesuchten Schülers
     * @return Der gefundene Schüler
     * @throws IllegalArgumentException Falls Schüler nicht gefunden wird
     */
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Der Schüler mit der ID " + id + " wurde nicht gefunden"));
    }

    /**
     * Abwesenheiten eines Schülers abrufen.
     *
     * @param studentId Die ID des Schülers
     * @return Liste aller Abwesenheiten des Schülers
     */
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    /**
     * Nächste Stundenpläne abrufen (max. 3), basierend auf dem aktuellen Tag/Uhrzeit.
     *
     * Diese Methode berechnet die nächsten drei anstehenden Unterrichtsstunden
     * für einen Schüler basierend auf dem aktuellen Datum und der Uhrzeit.
     *
     * @param studentId Die ID des Schülers
     * @return Liste der nächsten Unterrichtsstunden (maximal 3)
     * @throws IllegalArgumentException Falls Schüler nicht gefunden wird
     */
    public List<Schedule> getUpcomingSchedules(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler wurde nicht gefunden"));

        Class studentClass = student.getStudentClass();
        List<Schedule> allSchedules = scheduleRepository.findByStudentClassId(studentClass.getId());

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        Map<String, DayOfWeek> ziMap = Map.of(
                "Luni", DayOfWeek.MONDAY,
                "Marți", DayOfWeek.TUESDAY,
                "Miercuri", DayOfWeek.WEDNESDAY,
                "Joi", DayOfWeek.THURSDAY,
                "Vineri", DayOfWeek.FRIDAY,
                "Sâmbătă", DayOfWeek.SATURDAY,
                "Duminică", DayOfWeek.SUNDAY
        );

        return allSchedules.stream()
                .filter(schedule -> {
                    DayOfWeek scheduleDay = ziMap.get(schedule.getScheduleDay());
                    if (scheduleDay == null) return false;

                    if (scheduleDay.getValue() > today.getValue()) {
                        return true;
                    } else if (scheduleDay.getValue() == today.getValue()) {
                        try {
                            LocalTime start = LocalTime.parse(schedule.getStartTime(), timeFormatter);
                            return start.isAfter(now);
                        } catch (DateTimeParseException e) {
                            return false;
                        }
                    }
                    return false;
                })
                .sorted(Comparator
                        .comparing((Schedule s) -> ziMap.get(s.getScheduleDay()).getValue())
                        .thenComparing(s -> LocalTime.parse(s.getStartTime(), timeFormatter)))
                .limit(3)
                .collect(Collectors.toList());
    }

    /**
     * Schüler anhand seiner Eltern-ID finden.
     *
     * @param parentId Die ID des Elternteils
     * @return Optional mit dem gefundenen Schüler oder leer falls nicht gefunden
     */
    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    /**
     * Alle Schüler abrufen.
     *
     * @return Liste aller Schüler im System
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Schülerdaten aktualisieren.
     *
     * Diese Methode aktualisiert die Grunddaten eines Schülers und
     * kann auch eine neue Klassenzuordnung vornehmen.
     *
     * @param id Die ID des zu aktualisierenden Schülers
     * @param updatedStudent Der Schüler mit den neuen Daten
     * @return Der aktualisierte Schüler
     * @throws IllegalArgumentException Falls Schüler oder Klasse nicht gefunden wird
     */
    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Der Schüler wurde nicht gefunden"));

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());

        if (updatedStudent.getStudentClass() != null && updatedStudent.getStudentClass().getId() != null) {
            Class studentClass = classService.getClassById(updatedStudent.getStudentClass().getId())
                    .orElseThrow(() -> new RuntimeException("Klasse wurde nicht gefunden"));
            existingStudent.setStudentClass(studentClass);
        }

        return studentRepository.save(existingStudent);
    }

    /**
     * Schüler löschen (inkl. Noten, Tokens, User-Eintrag).
     *
     * Diese Methode führt eine vollständige Löschung eines Schülers durch,
     * einschließlich aller zugehörigen Daten wie Noten, Tokens und Benutzerdaten.
     *
     * @param id Die ID des zu löschenden Schülers
     * @throws IllegalArgumentException Falls Schüler nicht gefunden wird
     */
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schüler mit ID " + id + " wurde nicht gefunden"));

        gradeRepository.deleteByStudentId(student.getId());
        tokenRepository.deleteAllByUser_Id(student.getId());
        studentRepository.deleteById(student.getId());
        userRepository.deleteById(student.getId());
    }

    /**
     * Schüler in die nächste Klasse versetzen bzw. Absolventen verarbeiten.
     *
     * Diese Methode führt die jährliche Versetzung aller Schüler durch.
     * Schüler der 12. Klasse werden als Absolventen archiviert, alle anderen
     * werden in die nächsthöhere Klasse versetzt.
     */
    @Transactional
    public void advanceYear() {
        List<Class> allSchoolClasses = classRepository.findAll();

        for (Class currentSchoolClass : allSchoolClasses) {
            String className = currentSchoolClass.getName();

            if (className.startsWith("12")) {
                graduateStudents(currentSchoolClass);
            } else {
                moveStudentsToNextClass(currentSchoolClass);
            }
        }
    }

    /**
     * Schüler abschließen und in ehemalige Schülerliste einfügen.
     *
     * Diese private Methode verarbeitet Absolventen der 12. Klasse,
     * archiviert ihre Daten und entfernt sie aus der aktiven Schülerliste.
     *
     * @param currentSchoolClass Die Abschlussklasse
     */
    private void graduateStudents(Class currentSchoolClass) {
        List<Student> studentsInClass = studentRepository.findByStudentClass(currentSchoolClass);

        for (Student student : studentsInClass) {
            PastStudent pastStudent = new PastStudent(
                    null,
                    student.getName(),
                    currentSchoolClass.getSpecialization()
            );

            pastStudentRepository.save(pastStudent);
            studentRepository.delete(student);
        }
    }

    /**
     * Schüler in neue Klasse mit incrementierter Stufe verschieben.
     *
     * Diese private Methode versetzt Schüler in die nächsthöhere Klasse
     * und erstellt neue Klassen falls diese noch nicht existieren.
     *
     * @param currentSchoolClass Die aktuelle Klasse
     * @throws RuntimeException Falls der Klassenname ungültig ist
     */
    private void moveStudentsToNextClass(Class currentSchoolClass) {
        String className = currentSchoolClass.getName();
        String numericPart = className.replaceAll("^(\\d+).*", "$1");

        try {
            int currentYear = Integer.parseInt(numericPart);
            String newClassName = (currentYear + 1) + className.substring(numericPart.length());

            Class newSchoolClass = classRepository.findByName(newClassName)
                    .orElseGet(() -> createNewClass(newClassName, currentSchoolClass));

            List<Student> studentsInClass = studentRepository.findByStudentClass(currentSchoolClass);

            for (Student student : studentsInClass) {
                student.setStudentClass(newSchoolClass);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Der Klassenname beginnt nicht mit einer gültigen Zahl: " + className);
        }
    }

    /**
     * Neue Klasse erstellen, wenn sie nicht existiert.
     *
     * Diese private Methode erstellt eine neue Klasse für die Versetzung,
     * falls die Zielklasse noch nicht in der Datenbank existiert.
     *
     * @param newClassName Der Name der neuen Klasse
     * @param oldSchoolClass Die alte Klasse als Vorlage
     * @return Die neu erstellte Klasse
     */
    private Class createNewClass(String newClassName, Class oldSchoolClass) {
        Class newSchoolClass = new Class();
        newSchoolClass.setName(newClassName);
        newSchoolClass.setSpecialization(oldSchoolClass.getSpecialization());
        newSchoolClass.setClassTeacher(null);
        return classRepository.save(newSchoolClass);
    }

    /**
     * Schüler anhand Username finden.
     *
     * @param username Der Benutzername des gesuchten Schülers
     * @return Der gefundene Schüler oder null falls nicht gefunden
     */
    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    /**
     * Schüler anhand seiner User-ID abrufen.
     *
     * @param userId Die User-ID des gesuchten Schülers
     * @return Der gefundene Schüler
     * @throws RuntimeException Falls Schüler nicht gefunden wird
     */
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Schüler mit User-ID " + userId + " wurde nicht gefunden"));
    }

    /**
     * Schülerobjekt speichern.
     *
     * @param student Der zu speichernde Schüler
     * @return Der gespeicherte Schüler
     */
    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}