package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.email.EmailService;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.UserService;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChefService chefService;

    @Autowired
    private ClassService classService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // ✅ Autentificare utilizator (Oricine poate accesa)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        try {

            String username = credentials.get("username");
            String password = credentials.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username și parola sunt necesare"));
            }

            User user = userService.findByUsername(username).orElse(null);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("Parola introdusă din Postmnan la login: " + password);
            System.out.println("Parola stocată în DB: " + user.getPassword());
            System.out.println();
            System.out.println();

            Optional<User> testuser = userService.findByUsername(username);
            if (testuser.isPresent()) {
                System.out.println("Utilizator găsit: " + testuser.get().getUsername());
                System.out.println("Parola salvată în DB: " + testuser.get().getPassword());
            } else {
                System.out.println("Utilizatorul nu există!");
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Credentiale invalide"));
            }

            String token = jwtUtil.generateToken(username, user.getUserType());

            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare la autentificare: " + e.getMessage()));
        }
    }

    // ✅ Înregistrare student cu părinte (accesibil doar administratorului)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register-with-parent")
    public ResponseEntity<?> registerStudentWithParent(@RequestBody Map<String, Object> userData) {
        try {
            Map<String, Object> studentData = (Map<String, Object>) userData.get("student");
            if (studentData == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Detalii student lipsă"));
            }

            System.out.println("Student Data: " + studentData);
            System.out.println("Student Email Extracted: " + studentData.get("email"));


            Student student = new Student();
            student.setUserType(User.UserType.STUDENT);
            student.setName((String) studentData.get("name"));
            student.setEmail((String) studentData.get("email"));
            student.setPhoneNumber((String) studentData.get("phoneNumber"));
            String studentBaseName = ((String) studentData.get("name")).toLowerCase().replaceAll("\\s+", "_");
            student.setUsername(studentBaseName + ".student");
            String studentRawPassword = studentBaseName.replace(".", "") + "123!";
            student.setPassword(passwordEncoder.encode(studentRawPassword));

            // Setare clasa student
            Map<String, Object> studentClassData = (Map<String, Object>) studentData.get("studentClass");
            if (studentClassData != null) {
                Long classId = Long.parseLong(studentClassData.get("id").toString());

                // Încarcă clasa din baza de date
                Class studentSchoolClass = classService.getClassById(classId)
                        .orElseThrow(() -> new RuntimeException("Class with ID " + classId + " not found in database"));

                student.setStudentClass(studentSchoolClass);
            }


            // Setare părinte
            Map<String, Object> parentData = (Map<String, Object>) studentData.get("parent");
            if (parentData != null) {
                Parent parent = new Parent();
                String parentBaseName = ((String) parentData.get("motherName")).toLowerCase().replaceAll("\\s+", "_");
                parent.setUsername(parentBaseName + ".parent");
                String parentRawPassword = parentBaseName.replace(".", "") + "123!";
                parent.setPassword(passwordEncoder.encode(parentRawPassword));
                parent.setUserType(User.UserType.PARENT);
                parent.setMotherName((String) parentData.get("motherName"));
                parent.setMotherEmail((String) parentData.get("motherEmail"));
                parent.setMotherPhoneNumber((String) parentData.get("motherPhoneNumber"));
                parent.setFatherName((String) parentData.get("fatherName"));
                parent.setFatherEmail((String) parentData.get("fatherEmail"));
                parent.setFatherPhoneNumber((String) parentData.get("fatherPhoneNumber"));
                parent.setEmail((String) parentData.get("email"));

                student.setParent(parent);

                String studentResetLink = "http://localhost:8080/auth/reset-password?username=" + student.getUsername();
                emailService.sendResetPasswordEmail(student.getEmail(), student.getUsername(), studentResetLink);

                String parentResetLink = "http://localhost:8080/auth/reset-password?username=" + parent.getUsername();
                emailService.sendResetPasswordEmail(parent.getEmail(), parent.getFatherName(), parentResetLink);
            }
            System.out.println("Student înainte de salvare: " + student);

            userService.createUser(student);


            return ResponseEntity.ok(Map.of("message", "Student și părinte creați cu succes!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }

    // ✅ Doar ADMIN poate înregistra un profesor
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register-teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody Teacher teacher) {
        try {
            if (teacher.getName() == null || teacher.getSubject() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Numele și materia sunt necesare"));
            }

            String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", "_");
            teacher.setUsername(baseUsername + ".prof");
            String rawPassword = baseUsername.replace(".", "") + "123!";
            teacher.setPassword(passwordEncoder.encode(rawPassword));
            teacher.setUserType(User.UserType.TEACHER);

            userService.createUser(teacher);
            String resetLink = "http://localhost:8080/auth/reset-password?username=" + teacher.getUsername();
            emailService.sendResetPasswordEmail(teacher.getEmail(), teacher.getUsername(), resetLink);
            return ResponseEntity.ok(Map.of("message", "Profesor creat cu succes!", "username", teacher.getUsername()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Eroare: " + e.getMessage()));
        }
    }


    // ✅ Înregistrare ADMIN (Oricine poate accesa pentru a crea primul admin)
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");  // înlocuiește „username” cu „name”
            String email = request.get("email");

            if (name == null || email == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Numele și email-ul sunt necesare"));
            }

            String baseUsername = name.toLowerCase().replaceAll("\\s+", "_");
            String username = baseUsername + ".admin";
            String rawPassword = baseUsername.replace(".", "") + "123!";

            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setEmail(email);

            userService.createUser(admin);

            return ResponseEntity.ok(Map.of(
                    "message", "Admin creat cu succes!",
                    "username", username,
                    "password", rawPassword
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Eroare la crearea adminului: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/register-chef")
    public ResponseEntity<?> registerChef(@RequestBody Chef chef) {
        try {
            if (chef.getName() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Numele bucătăresei este necesar"));
            }

            // Generare username și parolă automat
            String baseUsername = chef.getName().toLowerCase().replaceAll("\\s+", "_");
            String username = baseUsername + ".chef";
            String rawPassword = baseUsername.replace("_", "") + "123!";

            chef.setUsername(username);
            chef.setPassword(passwordEncoder.encode(rawPassword));
            chef.setUserType(User.UserType.CHEF);

            // ATENȚIE: Salvăm chef-ul în tabelul `users`, nu doar în `chefs`
            userService.createUser(chef);
            String resetLink = "http://localhost:8080/auth/reset-password?username=" + username;
            emailService.sendResetPasswordEmail(chef.getEmail(), chef.getUsername(), resetLink);

            return ResponseEntity.ok(Map.of(
                    "message", "Bucătăreasă înregistrată cu succes!",
                    "username", username,
                    "password", rawPassword
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Eroare: " + e.getMessage())
            );
        }
    }
}
