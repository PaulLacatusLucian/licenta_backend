package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private AbsenceRepository absenceRepository; // Repository pentru absențe

    @Autowired
    private PastStudentRepository pastStudentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Transactional
    public Student saveStudentWithClass(Student studentDetails, Long classId) {
        // Caută clasa asociată
        Class studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Clasa cu ID-ul " + classId + " nu există"));

        // Asociază clasa studentului
        studentDetails.setStudentClass(studentClass);

        // Asociază părintele doar în cadrul studentului
        Parent parent = studentDetails.getParent();
        if (parent != null) {
            // Validări sau alte operațiuni asupra obiectului Parent, dacă sunt necesare
            // Nu mai setăm relația inversă
        }

        // Salvează studentul împreună cu părintele
        return studentRepository.save(studentDetails);
    }

    public Optional<Student> getStudentById(Long id) {
        // Caută studentul în baza de date și returnează inclusiv părintele
        return studentRepository.findById(id);
    }

    // Obține absențele studentului folosind AbsenceRepository
    public List<Absence> getAbsencesByStudentId(Long studentId) {
        return absenceRepository.findByStudentId(studentId);
    }

    // Exemplu pentru cursurile viitoare (poate fi extins ulterior)
    public List<Class> getUpcomingClasses(Long studentId) {
        // Înlocuiește cu logica ta pentru cursurile viitoare
        return Collections.emptyList(); // Deocamdată returnează o listă goală
    }

    public Optional<Student> getStudentByParentId(Long parentId) {
        return studentRepository.findByParentId(parentId);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(updatedStudent.getName());
                    existingStudent.setEmail(updatedStudent.getEmail());
                    existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());
                    existingStudent.setStudentClass(updatedStudent.getStudentClass());
                    return studentRepository.save(existingStudent);
                })
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
    }


    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        gradeRepository.deleteByStudentId(student.getId());

        // Acum poți șterge studentul în siguranță
        studentRepository.delete(student);
    }


    @Transactional
    public void advanceYear() {
        // Obține toate clasele
        List<Class> allClasses = classRepository.findAll();

        for (Class currentClass : allClasses) {
            String className = currentClass.getName();

            // Verifică dacă clasa este de tip "12X"
            if (className.startsWith("12")) {
                // Obține toți studenții din această clasă
                List<Student> studentsInClass = studentRepository.findByStudentClass(currentClass);

                for (Student student : studentsInClass) {
                    // Creează un nou PastStudent
                    PastStudent pastStudent = new PastStudent(
                            null,
                            student.getName(),
                            currentClass.getSpecialization()
                    );

                    // Salvează în tabela PastStudents
                    pastStudentRepository.save(pastStudent);

                    // Șterge studentul din tabela curentă
                    studentRepository.delete(student);
                }
            } else {
                // Crește anul pentru restul claselor
                try {
                    // Extrage partea numerică de la începutul numelui clasei
                    String numericPart = className.replaceAll("^(\\d+).*", "$1");
                    int currentYear = Integer.parseInt(numericPart);

                    // Creează un nou nume pentru clasă
                    String newClassName = (currentYear + 1) + className.substring(numericPart.length());

                    // Găsește sau creează noua clasă
                    Class newClass = classRepository.findByName(newClassName)
                            .orElseGet(() -> {
                                Class c = new Class();
                                c.setName(newClassName);
                                c.setSpecialization(currentClass.getSpecialization());
                                c.setClassTeacher(null); // Profesorul poate fi setat separat dacă e nevoie
                                return classRepository.save(c);
                            });

                    // Mută toți studenții în noua clasă
                    List<Student> studentsInClass = studentRepository.findByStudentClass(currentClass);

                    for (Student student : studentsInClass) {
                        student.setStudentClass(newClass); // Actualizează referința clasei
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Numele clasei nu începe cu un număr valid: " + className);
                }
            }
        }
    }
}

