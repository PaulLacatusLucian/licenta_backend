package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register-with-parent")
    public ResponseEntity<?> registerStudentWithParent(@RequestBody Map<String, Object> userData) {
        try {
            // Extragem datele studentului
            Map<String, Object> studentData = (Map<String, Object>) userData.get("student");
            if (studentData == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Detalii student lipsă"));
            }

            User studentUser = new User();
            studentUser.setUsername((String) studentData.get("username"));
            studentUser.setPassword((String) studentData.get("password"));
            studentUser.setName((String) studentData.get("name"));
            studentUser.setEmail((String) studentData.get("email"));
            studentUser.setPhoneNumber((String) studentData.get("phoneNumber"));
            studentUser.setUserType("student");

            // Creăm entitatea Student
            Student student = new Student();
            student.setName(studentUser.getName());
            student.setEmail(studentUser.getEmail());
            student.setPhoneNumber(studentUser.getPhoneNumber());

            // Setăm clasa studentului
            Map<String, Object> studentClassData = (Map<String, Object>) studentData.get("studentClass");
            if (studentClassData != null) {
                Long classId = Long.parseLong(studentClassData.get("id").toString());
                Class studentClass = new Class();
                studentClass.setId(classId);
                student.setStudentClass(studentClass);
            }

            // Setăm datele părinților
            Map<String, Object> parentData = (Map<String, Object>) studentData.get("parent");
            if (parentData != null) {
                Parent parent = new Parent();
                parent.setMotherName((String) parentData.get("motherName"));
                parent.setMotherEmail((String) parentData.get("motherEmail"));
                parent.setMotherPhoneNumber((String) parentData.get("motherPhoneNumber"));
                parent.setFatherName((String) parentData.get("fatherName"));
                parent.setFatherEmail((String) parentData.get("fatherEmail"));
                parent.setFatherPhoneNumber((String) parentData.get("fatherPhoneNumber"));

                student.setParent(parent); // Asociem părintele direct în student
            }

            // Asociem studentul cu utilizatorul
            studentUser.setStudent(student);

            // Salvăm utilizatorul student împreună cu părintele
            User createdStudentUser = userService.createUser(studentUser);

            return ResponseEntity.ok(Map.of(
                    "studentUser", createdStudentUser,
                    "message", "Student și părinți creați cu succes!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username și parola sunt necesare"));
            }

            User user = userService.findByUsername(username);

            // Validează utilizatorul și parola
            if (user != null && user.getPassword().equals(password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("username", user.getUsername());
                response.put("userType", user.getUserType());
                response.put("isEmployee", user.isEmployee());

                return ResponseEntity.ok(response);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credentiale invalide"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare la autentificare: " + e.getMessage()));
        }
    }



}

