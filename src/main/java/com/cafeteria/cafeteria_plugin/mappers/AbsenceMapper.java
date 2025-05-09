package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Absence;
import com.cafeteria.cafeteria_plugin.models.ClassSession;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AbsenceMapper {

    /**
     * Convertește un DTO în entitate, adăugând și profesorul care a înregistrat absența
     */
    public Absence toEntity(AbsenceDTO dto, Student student, ClassSession classSession, Teacher teacher) {
        Absence entity = new Absence();
        entity.setId(dto.getId());
        entity.setStudent(student);
        entity.setClassSession(classSession);
        // Aici ar trebui să adaugi entity.setTeacher(teacher) dacă ai adăugat câmpul în modelul Absence

        return entity;
    }

    /**
     * Versiune simplificată pentru compatibilitate cu codul existent
     */
    public Absence toEntity(AbsenceDTO dto, Student student, ClassSession classSession) {
        return toEntity(dto, student, classSession, classSession.getTeacher());
    }

    /**
     * Convertește o entitate în DTO, păstrând data sesiunii și profesorul care a dat absența
     */
    public AbsenceDTO toDto(Absence absence) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(absence.getId());
        dto.setClassSessionId(absence.getClassSession().getId());
        dto.setJustified(absence.getJustified());

        // Adăugăm data sesiunii (nu data curentă)
        LocalDateTime sessionDate = absence.getClassSession().getStartTime();
        dto.setSessionDate(sessionDate);

        // Adăugăm informații despre profesorul care a dat absența, nu dirigintele
        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(absence.getClassSession().getTeacher().getId());
        teacherDTO.setName(absence.getClassSession().getTeacher().getName());
        teacherDTO.setSubject(absence.getClassSession().getSubject());
        teacherDTO.setEmail(absence.getClassSession().getTeacher().getEmail());


        dto.setTeacherWhoMarkedAbsence(teacherDTO);

        // Informații despre student
        Student student = absence.getStudent();
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setUsername(student.getUsername());
        studentDTO.setPhoneNumber(student.getPhoneNumber());

        if (student.getStudentClass() != null) {
            studentDTO.setClassName(student.getStudentClass().getName());
            studentDTO.setClassSpecialization(student.getStudentClass().getSpecialization());

            if (student.getStudentClass().getClassTeacher() != null) {
                TeacherDTO classTeacherDTO = new TeacherDTO();
                classTeacherDTO.setId(student.getStudentClass().getClassTeacher().getId());
                classTeacherDTO.setName(student.getStudentClass().getClassTeacher().getName());
                classTeacherDTO.setSubject(student.getStudentClass().getClassTeacher().getSubject());
                classTeacherDTO.setUsername(student.getStudentClass().getClassTeacher().getUsername());
                classTeacherDTO.setEmail(student.getStudentClass().getClassTeacher().getEmail());

                studentDTO.setClassTeacher(classTeacherDTO);
            }
        }

        dto.setStudent(studentDTO);
        return dto;
    }
}