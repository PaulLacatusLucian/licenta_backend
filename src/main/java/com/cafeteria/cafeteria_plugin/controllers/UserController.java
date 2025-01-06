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

            // Creăm utilizatorul student
            Student student = new Student();
            student.setUsername((String) studentData.get("username"));
            student.setPassword((String) studentData.get("password"));
            student.setUserType("student");
            student.setName((String) studentData.get("name"));
            student.setEmail((String) studentData.get("email"));
            student.setPhoneNumber((String) studentData.get("phoneNumber"));

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
                parent.setUsername((String) parentData.get("username"));
                parent.setPassword((String) parentData.get("password"));
                parent.setUserType("parent");
                parent.setMotherName((String) parentData.get("motherName"));
                parent.setMotherEmail((String) parentData.get("motherEmail"));
                parent.setMotherPhoneNumber((String) parentData.get("motherPhoneNumber"));
                parent.setFatherName((String) parentData.get("fatherName"));
                parent.setFatherEmail((String) parentData.get("fatherEmail"));
                parent.setFatherPhoneNumber((String) parentData.get("fatherPhoneNumber"));

                student.setParent(parent); // Asociem părintele cu studentul
            }

            // Salvăm utilizatorii student și părinte
            userService.createUser(student); // Salvează studentul și cascadează părintele

            return ResponseEntity.ok(Map.of(
                    "message", "Student și părinte creați cu succes!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            System.out.println("Username primit: " + username);
            System.out.println("Parola primită: " + password);

            if (username == null || password == null) {
                System.out.println("Username sau parola lipsă");
                return ResponseEntity.badRequest().body(Map.of("message", "Username și parola sunt necesare"));
            }

            User user = userService.findByUsername(username);
            System.out.println("Utilizator găsit: " + (user != null ? user.getUsername() : "null"));

            if (user == null) {
                System.out.println("Utilizator inexistent");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utilizator inexistent"));
            }

            System.out.println("Parola utilizatorului din DB: " + user.getPassword());

            if (!user.getPassword().equals(password)) {
                System.out.println("Parolele nu se potrivesc");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Credentiale invalide"));
            }

            System.out.println("Autentificare reușită pentru utilizator: " + username);

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("userType", user.getUserType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare la autentificare: " + e.getMessage()));
        }
    }




}

