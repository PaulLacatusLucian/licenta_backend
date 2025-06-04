package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.email.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassSessionRepository classSessionRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private PastStudentRepository pastStudentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ClassService classService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    // Schüler in eine bestimmte Klasse speichern
    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        Class studentSchoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Die Klasse mit der ID " + classId + " existiert nicht"));

        studentDetails.setStudentClass(studentSchoolClass);
        return studentRepository.save(studentDetails);
    }

    // Schüler anhand seiner ID abrufen
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Der Schüler mit der ID " + id + " wurde nicht gefunden"));
    }

    // Abwesenheiten eines Schülers abrufen
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    // Nächste Stundenpläne abrufen (max. 3), basierend auf dem aktuellen Tag/Uhrzeit
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

    // Schüler anhand seiner Eltern-ID finden
    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    // Alle Schüler abrufen
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Schülerdaten aktualisieren
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

    // Schüler löschen (inkl. Noten, Tokens, User-Eintrag)
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schüler mit ID " + id + " wurde nicht gefunden"));

        gradeRepository.deleteByStudentId(student.getId());
        tokenRepository.deleteAllByUser_Id(student.getId());
        studentRepository.deleteById(student.getId());
        userRepository.deleteById(student.getId());
    }

    // Schüler in die nächste Klasse versetzen bzw. Absolventen verarbeiten
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

    // Schüler abschließen und in ehemalige Schülerliste einfügen
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

    // Schüler in neue Klasse mit incrementierter Stufe verschieben
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

    // Neue Klasse erstellen, wenn sie nicht existiert
    private Class createNewClass(String newClassName, Class oldSchoolClass) {
        Class newSchoolClass = new Class();
        newSchoolClass.setName(newClassName);
        newSchoolClass.setSpecialization(oldSchoolClass.getSpecialization());
        newSchoolClass.setClassTeacher(null);
        return classRepository.save(newSchoolClass);
    }

    // Schüler anhand Username finden
    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    // Schüler anhand seiner User-ID abrufen
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Schüler mit User-ID " + userId + " wurde nicht gefunden"));
    }

    // Schülerobjekt speichern
    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}
