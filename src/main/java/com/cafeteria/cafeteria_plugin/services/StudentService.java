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


    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        Class studentSchoolClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        studentDetails.setStudentClass(studentSchoolClass);
        return studentRepository.save(studentDetails);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Studentul cu ID-ul " + id + " nu există"));
    }

    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }


    public List<Schedule> getUpcomingSchedules(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

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



    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Studentul nu a fost găsit"));

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());

        if (updatedStudent.getStudentClass() != null && updatedStudent.getStudentClass().getId() != null) {
            Class studentClass = classService.getClassById(updatedStudent.getStudentClass().getId())
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            existingStudent.setStudentClass(studentClass);
        }

        return studentRepository.save(existingStudent);
    }


    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        // 1. Șterge notele
        gradeRepository.deleteByStudentId(student.getId());

        // 2. Șterge TOATE tokenurile asociate utilizatorului
        tokenRepository.deleteAllByUser_Id(student.getId());

        // 3. Șterge Student-ul (entitate derivată)
        studentRepository.deleteById(student.getId());

        // 4. Șterge User-ul
        userRepository.deleteById(student.getId());
    }


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
            throw new RuntimeException("Numele clasei nu începe cu un număr valid: " + className);
        }
    }

    private Class createNewClass(String newClassName, Class oldSchoolClass) {
        Class newSchoolClass = new Class();
        newSchoolClass.setName(newClassName);
        newSchoolClass.setSpecialization(oldSchoolClass.getSpecialization());
        newSchoolClass.setClassTeacher(null);
        return classRepository.save(newSchoolClass);
    }

    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public Student getStudentByUserId(Long userId) {
        return studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Student not found with userId: " + userId));
    }

    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

}
