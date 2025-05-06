//package com.cafeteria.cafeteria_plugin.contollers;
//
//import com.cafeteria.cafeteria_plugin.controllers.AbsenceController;
//import com.cafeteria.cafeteria_plugin.dtos.AbsenceDTO;
//import com.cafeteria.cafeteria_plugin.mappers.AbsenceMapper;
//import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
//import com.cafeteria.cafeteria_plugin.mappers.TeacherMapper;
//import com.cafeteria.cafeteria_plugin.models.Absence;
//import com.cafeteria.cafeteria_plugin.models.ClassSession;
//import com.cafeteria.cafeteria_plugin.models.Student;
//import com.cafeteria.cafeteria_plugin.models.Teacher;
//import com.cafeteria.cafeteria_plugin.services.AbsenceService;
//import com.cafeteria.cafeteria_plugin.services.ClassSessionService;
//import com.cafeteria.cafeteria_plugin.services.StudentService;
//import com.cafeteria.cafeteria_plugin.services.TeacherService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class AbsenceControllerTest {
//
//    @Mock private AbsenceService absenceService;
//    @Mock private ClassSessionService classSessionService;
//    @Mock private StudentService studentService;
//    @Mock private TeacherService teacherService;
//    @Mock private AbsenceMapper absenceMapper;
//    @Mock private StudentMapper studentMapper;
//    @Mock private TeacherMapper teacherMapper;
//
//    @InjectMocks
//    private AbsenceController absenceController;
//
//    private AbsenceDTO absenceDTO;
//    private Absence absence;
//    private Student student;
//    private Teacher teacher;
//    private ClassSession session;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        student = new Student();
//        student.setId(10L);
//
//        teacher = new Teacher();
//        teacher.setId(20L);
//
//        session = new ClassSession();
//        session.setId(30L);
//
//        absenceDTO = new AbsenceDTO();
//        absenceDTO.setId(1L);
//        absenceDTO.setDate(LocalDate.now());
//        absenceDTO.setSubject("Math");
//        absenceDTO.setStudent(studentMapper.toDTO(student));
//        absenceDTO.setTeacher(teacherMapper.toDto(teacher));
//        absenceDTO.setClassSessionId(session.getId());
//
//        absence = new Absence();
//        absence.setId(1L);
//        absence.setDate(absenceDTO.getDate());
//        absence.setSubject(absenceDTO.getSubject());
//        absence.setStudent(student);
//        absence.setTeacher(teacher);
//        absence.setClassSession(session);
//    }
//
//    @Test
//    public void testAddAbsence() {
//        when(studentService.getStudentById(student.getId())).thenReturn(student);
//        when(teacherService.getTeacherById(teacher.getId())).thenReturn(teacher);
//        when(classSessionService.getSessionById(session.getId())).thenReturn(session);
//        when(absenceMapper.toEntity(absenceDTO, student, session, teacher)).thenReturn(absence);
//        when(absenceService.addAbsence(absence)).thenReturn(absence);
//        when(absenceMapper.toDto(absence)).thenReturn(absenceDTO);
//
//        ResponseEntity<AbsenceDTO> response = absenceController.addAbsence(absenceDTO);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(absenceDTO, response.getBody());
//    }
//
//    @Test
//    public void testGetAllAbsences() {
//        List<Absence> absences = Arrays.asList(absence);
//        List<AbsenceDTO> dtoList = Arrays.asList(absenceDTO);
//
//        when(absenceService.getAllAbsences()).thenReturn(absences);
//        when(absenceMapper.toDto(absence)).thenReturn(absenceDTO);
//
//        ResponseEntity<List<AbsenceDTO>> response = absenceController.getAllAbsences();
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(dtoList, response.getBody());
//    }
//
//    @Test
//    public void testGetAbsenceById() {
//        when(absenceService.getAbsenceById(1L)).thenReturn(Optional.of(absence));
//        when(absenceMapper.toDto(absence)).thenReturn(absenceDTO);
//
//        ResponseEntity<AbsenceDTO> response = absenceController.getAbsenceById(1L);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(absenceDTO, response.getBody());
//    }
//
//    @Test
//    public void testUpdateAbsence() {
//        Absence updatedAbsence = new Absence();
//        updatedAbsence.setId(1L);
//        updatedAbsence.setDate(LocalDate.now());
//        updatedAbsence.setSubject("Physics");
//        updatedAbsence.setStudent(student);
//        updatedAbsence.setTeacher(teacher);
//        updatedAbsence.setClassSession(session);
//
//        AbsenceDTO updatedDTO = new AbsenceDTO();
//        updatedDTO.setId(1L);
//        updatedDTO.setDate(updatedAbsence.getDate());
//        updatedDTO.setSubject("Physics");
//        absenceDTO.setStudent(studentMapper.toDTO(student));
//        absenceDTO.setTeacher(teacherMapper.toDto(teacher));
//        updatedDTO.setClassSessionId(session.getId());
//
//        when(studentService.getStudentById(student.getId())).thenReturn(student);
//        when(teacherService.getTeacherById(teacher.getId())).thenReturn(teacher);
//        when(classSessionService.getSessionById(session.getId())).thenReturn(session);
//        when(absenceMapper.toEntity(updatedDTO, student, session, teacher)).thenReturn(updatedAbsence);
//        when(absenceService.updateAbsence(1L, updatedAbsence)).thenReturn(updatedAbsence);
//        when(absenceMapper.toDto(updatedAbsence)).thenReturn(updatedDTO);
//
//        ResponseEntity<AbsenceDTO> response = absenceController.updateAbsence(1L, updatedDTO);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(updatedDTO, response.getBody());
//    }
//
//    @Test
//    public void testDeleteAbsence() {
//        doNothing().when(absenceService).deleteAbsence(1L);
//
//        ResponseEntity<Void> response = absenceController.deleteAbsence(1L);
//
//        assertEquals(204, response.getStatusCodeValue());
//    }
//
//    @Test
//    public void testAddAbsenceToSession() {
//        when(classSessionService.getSessionById(session.getId())).thenReturn(session);
//        when(studentService.getStudentById(student.getId())).thenReturn(student);
//        when(teacherService.getTeacherById(teacher.getId())).thenReturn(teacher);
//        when(absenceMapper.toEntity(absenceDTO, student, session, teacher)).thenReturn(absence);
//        when(absenceService.addAbsence(absence)).thenReturn(absence);
//        when(absenceMapper.toDto(absence)).thenReturn(absenceDTO);
//
//        ResponseEntity<AbsenceDTO> response = absenceController.addAbsenceToSession(session.getId(), absenceDTO);
//
//        assertEquals(201, response.getStatusCodeValue());
//        assertEquals(absenceDTO, response.getBody());
//    }
//}
