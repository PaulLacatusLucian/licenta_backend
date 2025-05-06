package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.*;
import com.cafeteria.cafeteria_plugin.email.EmailService;
import com.cafeteria.cafeteria_plugin.mappers.GradeMapper;
import com.cafeteria.cafeteria_plugin.mappers.OrderHistoryMapper;
import com.cafeteria.cafeteria_plugin.mappers.ParentMapper;
import com.cafeteria.cafeteria_plugin.mappers.StudentMapper;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.Teacher;
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

@RestController
@RequestMapping("/parents")
public class ParentController {

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
    private OrderHistoryMapper orderHistoryMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private JwtUtil jwtUtil;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        List<ParentDTO> parentDTOs = parentService.getAllParents()
                .stream()
                .map(parentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(parentDTOs);
    }


    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ParentDTO> getParentById(@PathVariable Long id) {
        Parent parent = parentService.getParentById(id);
        return ResponseEntity.ok(parentMapper.toDto(parent));
    }


    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ParentDTO> updateParent(@PathVariable Long id, @RequestBody ParentDTO parentDTO) {
        Parent updatedParent = parentService.updateParent(id, parentMapper.toEntity(parentDTO));
        return ResponseEntity.ok(parentMapper.toDto(updatedParent));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }


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

    @PreAuthorize("hasRole('PARENT')")
    @PostMapping("/me/profile-image")
    public ResponseEntity<String> uploadParentProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("profileImage") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Parent parent = parentService.findByUsername(username);

            if (parent == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Parent not found");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

}
