package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.services.UserService;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ChefService chefService;

    public UserController(UserService userService, ChefService chefService) {
        this.userService = userService;
        this.chefService = chefService;
    }

    // ✅ Înregistrare student cu părinte
    @PostMapping("/register-with-parent")
    public ResponseEntity<?> registerStudentWithParent(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> studentData = (Map<String, Object>) userData.get("student");
            if (studentData == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Detalii student lipsă"));
            }

            Student student = new Student();
            student.setUsername((String) studentData.get("username"));
            student.setPassword((String) studentData.get("password"));
            student.setUserType("student");
            student.setName((String) studentData.get("name"));
            student.setEmail((String) studentData.get("email"));
            student.setPhoneNumber((String) studentData.get("phoneNumber"));

            // Setare clasa student
            Map<String, Object> studentClassData = (Map<String, Object>) studentData.get("studentClass");
            if (studentClassData != null) {
                Long classId = Long.parseLong(studentClassData.get("id").toString());
                Class studentClass = new Class();
                studentClass.setId(classId);
                student.setStudentClass(studentClass);
            }

            // Setare părinte
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

                student.setParent(parent);
            }

            userService.createUser(student);

            return ResponseEntity.ok(Map.of("message", "Student și părinte creați cu succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }

    // ✅ Autentificare utilizator
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username și parola sunt necesare"));
            }

            User user = userService.findByUsername(username);
            if (user == null || !user.getPassword().equals(password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credentiale invalide"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("userType", user.getUserType());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare la autentificare: " + e.getMessage()));
        }
    }

    // ✅ Înregistrare profesor
    @PostMapping("/register-teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody Map<String, String> teacherData) {
        try {
            if (!teacherData.containsKey("name") || !teacherData.containsKey("subject")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Numele și materia sunt necesare pentru profesor"));
            }

            Teacher teacher = new Teacher();
            teacher.setName(teacherData.get("name"));
            teacher.setSubject(teacherData.get("subject"));

            String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
            teacher.setUsername(baseUsername + ".prof");
            teacher.setPassword(baseUsername.replace(".", "_") + "123!");
            teacher.setUserType("teacher");

            userService.createUser(teacher);

            return ResponseEntity.ok(Map.of(
                    "message", "Profesor creat cu succes!",
                    "username", teacher.getUsername(),
                    "password", teacher.getPassword()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }

    // ✅ Înregistrare bucătăreasă
    @PostMapping("/register-chef")
    public ResponseEntity<?> registerChef(@RequestBody Map<String, String> chefData) {
        try {
            if (!chefData.containsKey("name")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Numele bucătăresei este necesar"));
            }

            Chef chef = new Chef();
            chef.setName(chefData.get("name"));

            String baseUsername = chef.getName().toLowerCase().replaceAll("\\s+", ".");
            chef.setUsername(baseUsername + ".chef");
            chef.setPassword(baseUsername.replace(".", "_") + "123!");
            chef.setUserType("chef");

            chefService.createChef(chef);

            return ResponseEntity.ok(Map.of("message", "Bucătăreasă înregistrată cu succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }

    // ✅ Obținerea tuturor bucătăreselor
    @GetMapping("/chefs")
    public ResponseEntity<List<Chef>> getAllChefs() {
        List<Chef> chefs = chefService.getAllChefs();
        return ResponseEntity.ok(chefs);
    }

    // ✅ Ștergerea unei bucătărese după ID
    @DeleteMapping("/chefs/{id}")
    public ResponseEntity<Void> deleteChef(@PathVariable Long id) {
        boolean deleted = chefService.deleteChef(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
