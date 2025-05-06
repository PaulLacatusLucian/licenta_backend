package com.cafeteria.cafeteria_plugin;

import com.cafeteria.cafeteria_plugin.email.PasswordResetService;
import com.cafeteria.cafeteria_plugin.email.PasswordResetToken;
import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.repositories.*;
import com.cafeteria.cafeteria_plugin.services.ClassService;
import com.cafeteria.cafeteria_plugin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Database seeder to populate initial teacher and class data.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassService classService;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordResetService passwordResetService;

    // Set pentru a urmări username-urile deja folosite
    private final Set<String> usedUsernames = new HashSet<>();
    private final Set<String> usedEmails = new HashSet<>();

    // Categoriile și materiile din frontend
    private final Map<String, List<String>> subjectsByCategory = Map.of(
            "Reale", List.of("Informatica", "Matematica", "Fizica", "Chimie", "Biologie"),
            "Umane", List.of("Istorie", "Geografie", "Romana", "Engleza", "Germana", "Italiana", "Latina", "Franceza"),
            "Arte și Sport", List.of("Educatie Fizica", "Arte Vizuale", "Muzica"),
            "Altele", List.of("Religie", "Psihologie", "Economie", "Filosofie")
    );

    // Specializări pentru liceu
    private final List<String> specializations = List.of(
            "Matematica-Informatica",
            "Matematica-Informatica-Bilingv",
            "Filologie",
            "Bio-Chimie"
    );

    // Prenume pentru generare aleatorie
    private final List<String> boyFirstNames = List.of(
            "Alexandru", "Andrei", "Mihai", "Stefan", "Gabriel", "Cristian", "David", "Teodor", "Robert", "Nicolae",
            "Radu", "Victor", "George", "Daniel", "Florin", "Adrian", "Ionut", "Dragos", "Marian", "Catalin"
    );

    private final List<String> girlFirstNames = List.of(
            "Maria", "Ana", "Elena", "Ioana", "Alexandra", "Andreea", "Sofia", "Gabriela", "Antonia", "Daria",
            "Diana", "Raluca", "Mihaela", "Cristina", "Alina", "Simona", "Denisa", "Daniela", "Laura", "Monica"
    );

    // Nume de familie pentru generare aleatorie
    private final List<String> lastNames = List.of(
            "Popescu", "Ionescu", "Popa", "Constantin", "Stan", "Gheorghe", "Rusu", "Marin", "Dumitru", "Stoica",
            "Dinu", "Diaconu", "Georgescu", "Ungureanu", "Vasile", "Anghel", "Barbu", "Tudor", "Moldovan", "Serban",
            "Neagu", "Cristea", "Enache", "Dobre", "Mocanu", "Ene", "Preda", "Alexandru", "Vlad", "Florea"
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Putem dezactiva executarea automată prin comentarea acestor linii
            // Încărcăm username-urile existente în cache
            userRepository.findAll().forEach(user -> {
                usedUsernames.add(user.getUsername());
                usedEmails.add(user.getEmail());
            });

            createAdmin();
            List<Teacher> teachers = seedTeachers();
            List<Class> classes = seedClasses(teachers);
            seedStudentsWithParents(classes);
        } catch (Exception e) {
            System.err.println("Eroare în timpul populării bazei de date: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Metodă pentru crearea unui administrator implicit
     */
    private void createAdmin() {
        try {
            // Verificăm dacă există deja un admin
            if (userService.findByUsername("admin_user.admin").isPresent()) {
                System.out.println("Admin deja există, se sare peste crearea lui.");
                return;
            }

            // Creăm un admin nou
            Admin admin = new Admin();
            admin.setUsername("admin_user.admin");
            admin.setPassword(passwordEncoder.encode("admin123!"));
            admin.setEmail("admin.admin");
            admin.setUserType(User.UserType.ADMIN);

            userService.createUser(admin);
            usedUsernames.add(admin.getUsername());
            usedEmails.add(admin.getEmail());

            System.out.println("✅ Admin default creat: admin_user.admin / admin123!");
        } catch (Exception e) {
            System.err.println("Eroare la crearea adminului: " + e.getMessage());
        }
    }

    /**
     * Metodă pentru popularea bazei de date cu profesori.
     * @return Lista de profesori creați
     */
    @Transactional
    public List<Teacher> seedTeachers() {
        System.out.println("Populare bază de date cu profesori...");
        List<Teacher> createdTeachers = new ArrayList<>();

        // Lista de profesori de diferite tipuri
        List<Map<String, Object>> teachersData = new ArrayList<>();

        // Profesori pentru liceu și gimnaziu (TEACHER)
        teachersData.addAll(Arrays.asList(
                Map.of(
                        "name", "Maria Popescu",
                        "email", "maria_popescu.prof",
                        "subject", "Matematica",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Ion Ionescu",
                        "email", "ion.ionescu@scoala.ro",
                        "subject", "Fizica",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Ana Dumitrescu",
                        "email", "ana.dumitrescu@scoala.ro",
                        "subject", "Romana",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Mihai Stanescu",
                        "email", "mihai.stanescu@scoala.ro",
                        "subject", "Istorie",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Elena Vasilescu",
                        "email", "elena.vasilescu@scoala.ro",
                        "subject", "Biologie",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Florin Dumitrache",
                        "email", "florin.dumitrache@scoala.ro",
                        "subject", "Informatica",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Carmen Stoica",
                        "email", "carmen.stoica@scoala.ro",
                        "subject", "Engleza",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "George Popa",
                        "email", "george.popa@scoala.ro",
                        "subject", "Educatie Fizica",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Adrian Moisescu",
                        "email", "adrian.moisescu@scoala.ro",
                        "subject", "Chimie",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Simona Radulescu",
                        "email", "simona.radulescu@scoala.ro",
                        "subject", "Muzica",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Valentin Constantinescu",
                        "email", "valentin.constantinescu@scoala.ro",
                        "subject", "Geografie",
                        "type", TeacherType.TEACHER
                ),
                Map.of(
                        "name", "Diana Manolescu",
                        "email", "diana.manolescu@scoala.ro",
                        "subject", "Arte Vizuale",
                        "type", TeacherType.TEACHER
                )
        ));

        // Educatori pentru școala primară (EDUCATOR)
        teachersData.addAll(Arrays.asList(
                Map.of(
                        "name", "Ioana Marinescu",
                        "email", "ioana.marinescu@scoala.ro",
                        "subject", "Învățător", // Educatorii au nevoie de ceva în câmpul subject
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Gabriela Dinu",
                        "email", "gabriela.dinu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Cristina Popescu",
                        "email", "cristina.popescu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Alexandru Munteanu",
                        "email", "alexandru.munteanu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Raluca Dragomir",
                        "email", "raluca.dragomir@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Teodora Pavelescu",
                        "email", "teodora.pavelescu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Oana Mitrache",
                        "email", "oana.mitrache@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Daniel Iliescu",
                        "email", "daniel.iliescu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Mirela Tomescu",
                        "email", "mirela.tomescu@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                ),
                Map.of(
                        "name", "Ileana Iorga",
                        "email", "ileana.iorga@scoala.ro",
                        "subject", "Învățător",
                        "type", TeacherType.EDUCATOR
                )
        ));

        // Creare profesori în baza de date direct prin UserService
        for (Map<String, Object> teacherData : teachersData) {
            Teacher teacher = new Teacher();
            teacher.setName((String) teacherData.get("name"));
            teacher.setEmail((String) teacherData.get("email"));
            teacher.setSubject((String) teacherData.get("subject"));
            teacher.setType((TeacherType) teacherData.get("type"));

            // Verificăm dacă emailul există deja
            if (usedEmails.contains(teacher.getEmail())) {
                System.out.println("Email-ul " + teacher.getEmail() + " există deja, se sare peste profesorul " + teacher.getName());
                continue;
            }

            // Setare tip utilizator
            teacher.setUserType(User.UserType.TEACHER);

            // Generare username și parolă conform logicii TeacherController
            String baseUsername = teacher.getName().toLowerCase().replaceAll("\\s+", ".");
            String username = baseUsername + ".prof";
            int counter = 1;

            // Verificare dacă username-ul există deja și generare alternativă
            while (usedUsernames.contains(username)) {
                username = baseUsername + counter + ".prof";
                counter++;
            }

            teacher.setUsername(username);
            String rawPassword = username.replace(".", "_") + "123!";
            teacher.setPassword(passwordEncoder.encode(rawPassword));

            try {
                Teacher createdTeacher = (Teacher) userService.createUser(teacher);
                createdTeachers.add(createdTeacher);

                // Adăugăm username-ul și email-ul în lista celor folosite
                usedUsernames.add(teacher.getUsername());
                usedEmails.add(teacher.getEmail());

                System.out.println("Profesor creat: " + teacher.getUsername() +
                        " (Parola: " + rawPassword + ") - " +
                        (teacher.getType() == TeacherType.EDUCATOR ? "Educator" : "Materie: " + teacher.getSubject()));
            } catch (Exception e) {
                System.err.println("Eroare la crearea profesorului " + teacher.getName() + ": " + e.getMessage());
            }
        }

        System.out.println("Populare profesori finalizată!");
        return createdTeachers;
    }

    /**
     * Metodă pentru popularea bazei de date cu clase.
     * @param teachers Lista de profesori disponibili pentru a fi atribuiți claselor
     * @return Lista claselor create
     */
    @Transactional
    public List<Class> seedClasses(List<Teacher> teachers) {
        System.out.println("Populare bază de date cu clase...");
        List<Class> createdClasses = new ArrayList<>();

        if (teachers == null || teachers.isEmpty()) {
            System.out.println("Nu există profesori disponibili pentru a crea clase.");
            return createdClasses;
        }

        // Grupăm profesorii după tip pentru a-i putea selecta ușor
        Map<TeacherType, List<Teacher>> teachersByType = new HashMap<>();
        teachersByType.put(TeacherType.EDUCATOR, new ArrayList<>());
        teachersByType.put(TeacherType.TEACHER, new ArrayList<>());

        for (Teacher teacher : teachers) {
            teachersByType.get(teacher.getType()).add(teacher);
        }

        // 1. Clase de învățământ primar (0-4) - create doar dacă există educatori disponibili
        createdClasses.addAll(createPrimaryClasses(teachersByType.get(TeacherType.EDUCATOR)));

        // 2. Clase de gimnaziu (5-8) - create doar dacă există profesori disponibili
        createdClasses.addAll(createMiddleClasses(teachersByType.get(TeacherType.TEACHER)));

        // 3. Clase de liceu (9-12) - create doar dacă există profesori disponibili
        createdClasses.addAll(createHighClasses(teachersByType.get(TeacherType.TEACHER)));

        System.out.println("Populare clase finalizată!");
        return createdClasses;
    }

    private List<Class> createPrimaryClasses(List<Teacher> educators) {
        List<Class> createdClasses = new ArrayList<>();
        if (educators.isEmpty()) {
            System.out.println("Nu există învățători disponibili pentru a crea clase primare.");
            return createdClasses;
        }

        // Pentru fiecare an de studiu primar, creăm clase bazate pe învățătorii disponibili
        int teacherCount = educators.size();
        int classesPerYear = Math.max(1, teacherCount / 5); // Distribuim învățătorii uniform pe anii 0-4

        for (int year = 0; year <= 4; year++) {
            for (char section = 'A'; section < 'A' + classesPerYear && !educators.isEmpty(); section++) {
                String className = year + String.valueOf(section);

                // Luăm primul educator disponibil
                Teacher classTeacher = educators.remove(0);

                try {
                    // Creare clasă primară direct prin ClassService
                    Class newClass = new Class();
                    newClass.setName(className);
                    newClass.setEducationLevel(EducationLevel.PRIMARY);
                    newClass.setClassTeacher(classTeacher);

                    Class createdClass = classService.addClass(newClass);
                    createdClasses.add(createdClass);
                    System.out.println("Clasă primară creată: " + className + " cu învățătorul " + classTeacher.getName());
                } catch (Exception e) {
                    System.err.println("Eroare la crearea clasei " + className + ": " + e.getMessage());
                }
            }
        }
        return createdClasses;
    }

    private List<Class> createMiddleClasses(List<Teacher> teachers) {
        List<Class> createdClasses = new ArrayList<>();
        if (teachers.isEmpty()) {
            System.out.println("Nu există profesori disponibili pentru a crea clase gimnaziale.");
            return createdClasses;
        }

        // Pentru fiecare an de studiu gimnazial, creăm clase bazate pe profesorii disponibili
        int teacherCount = Math.min(8, teachers.size()); // Limităm pentru clase gimnaziale
        int classesPerYear = Math.max(1, teacherCount / 4); // Distribuim profesorii uniform pe anii 5-8

        for (int year = 5; year <= 8; year++) {
            for (char section = 'A'; section < 'A' + classesPerYear && !teachers.isEmpty(); section++) {
                String className = year + String.valueOf(section);

                // Luăm primul profesor disponibil
                Teacher classTeacher = teachers.remove(0);

                try {
                    // Creare clasă gimnazială direct prin ClassService
                    Class newClass = new Class();
                    newClass.setName(className);
                    newClass.setEducationLevel(EducationLevel.MIDDLE);
                    newClass.setClassTeacher(classTeacher);

                    Class createdClass = classService.addClass(newClass);
                    createdClasses.add(createdClass);
                    System.out.println("Clasă gimnazială creată: " + className + " cu dirigintele " + classTeacher.getName());
                } catch (Exception e) {
                    System.err.println("Eroare la crearea clasei " + className + ": " + e.getMessage());
                }
            }
        }
        return createdClasses;
    }

    private List<Class> createHighClasses(List<Teacher> teachers) {
        List<Class> createdClasses = new ArrayList<>();
        if (teachers.isEmpty()) {
            System.out.println("Nu există profesori disponibili pentru a crea clase de liceu.");
            return createdClasses;
        }

        // Pentru fiecare an de studiu liceal, creăm clase bazate pe profesorii disponibili
        int teacherCount = teachers.size();
        int totalYears = 4; // Anii 9-12
        int classesPerYear = Math.max(1, Math.min(3, teacherCount / totalYears)); // Maxim 3 clase per an

        Random random = new Random();
        for (int year = 9; year <= 12 && !teachers.isEmpty(); year++) {
            int classesToCreate = Math.min(classesPerYear, teachers.size());

            for (char section = 'A'; section < 'A' + classesToCreate && !teachers.isEmpty(); section++) {
                String className = year + String.valueOf(section);

                // Alegem o specializare random pentru această clasă
                String specialization = specializations.get(random.nextInt(specializations.size()));

                // Luăm primul profesor disponibil
                Teacher classTeacher = teachers.remove(0);

                try {
                    // Creare clasă de liceu direct prin ClassService
                    Class newClass = new Class();
                    newClass.setName(className);
                    newClass.setEducationLevel(EducationLevel.HIGH);
                    newClass.setSpecialization(specialization);
                    newClass.setClassTeacher(classTeacher);

                    Class createdClass = classService.addClass(newClass);
                    createdClasses.add(createdClass);
                    System.out.println("Clasă liceu creată: " + className + " (" + specialization + ") cu dirigintele " + classTeacher.getName());
                } catch (Exception e) {
                    System.err.println("Eroare la crearea clasei " + className + ": " + e.getMessage());
                }
            }
        }
        return createdClasses;
    }

    /**
     * Metodă pentru popularea bazei de date cu elevi și părinții lor
     * Folosind aceeași logică din controller-ul AuthController (register-with-parent)
     */
    @Transactional
    private void seedStudentsWithParents(List<Class> classes) {
        System.out.println("Populare bază de date cu elevi și părinți...");

        if (classes == null || classes.isEmpty()) {
            System.out.println("Nu există clase disponibile pentru a crea elevi.");
            return;
        }

        Random random = new Random();
        int totalStudentsToCreate = 0;
        int successfullyCreated = 0;

        // Pentru fiecare clasă, creăm 5-15 elevi cu părinții lor
        for (Class schoolClass : classes) {
            int studentCount = random.nextInt(6) + 5; // 5-10 elevi per clasă pentru a reduce numărul total
            totalStudentsToCreate += studentCount;

            for (int i = 0; i < studentCount; i++) {
                try {
                    // Generare nume aleatoriu pentru elev
                    boolean isBoy = random.nextBoolean();
                    String firstName = isBoy ?
                            boyFirstNames.get(random.nextInt(boyFirstNames.size())) :
                            girlFirstNames.get(random.nextInt(girlFirstNames.size()));
                    String lastName = lastNames.get(random.nextInt(lastNames.size()));
                    String studentName = firstName + " " + lastName;

                    // === CREARE STUDENT ===
                    Student student = new Student();
                    student.setUserType(User.UserType.STUDENT);
                    student.setName(studentName);

                    // Email și telefon generate
                    // Adăugăm un identificator unic pentru a evita duplicate
                    String uniqueId = UUID.randomUUID().toString().substring(0, 4);
                    String studentEmail = (firstName.toLowerCase() + "." + lastName.toLowerCase() + uniqueId + "@elev.scoala.ro").replaceAll("[^a-z0-9.@]", "");
                    student.setEmail(studentEmail);
                    student.setPhoneNumber("07" + String.format("%08d", random.nextInt(100000000)));

                    // Username și parolă
                    String studentBaseName = studentName.toLowerCase().replaceAll("\\s+", "_");
                    String studentUsername = studentBaseName + uniqueId + ".student";
                    student.setUsername(studentUsername);
                    String studentRawPassword = studentUsername.replace(".", "_") + "123!";
                    student.setPassword(passwordEncoder.encode(studentRawPassword));

                    // === SETARE CLASĂ ===
                    student.setStudentClass(schoolClass);

                    // === CREARE ȘI SALVARE PĂRINTE ===
                    // Generare nume ale părinților
                    String motherFirstName = girlFirstNames.get(random.nextInt(girlFirstNames.size()));
                    String fatherFirstName = boyFirstNames.get(random.nextInt(boyFirstNames.size()));
                    String motherName = motherFirstName + " " + lastName;
                    String fatherName = fatherFirstName + " " + lastName;

                    Parent parent = new Parent();

                    // Username și parolă - adăugăm identificator unic
                    String parentBaseName = motherName.toLowerCase().replaceAll("\\s+", "_");
                    String parentUsername = parentBaseName + uniqueId + ".parent";
                    parent.setUsername(parentUsername);
                    String parentRawPassword = parentUsername.replace(".", "_") + "123!";
                    parent.setPassword(passwordEncoder.encode(parentRawPassword));

                    // Informații de bază
                    parent.setUserType(User.UserType.PARENT);
                    parent.setMotherName(motherName);
                    parent.setFatherName(fatherName);

                    // Emailuri și telefoane cu identificator unic
                    String motherEmail = (motherFirstName.toLowerCase() + "." + lastName.toLowerCase() + uniqueId + "@exemplu.com").replaceAll("[^a-z0-9.@]", "");
                    String fatherEmail = (fatherFirstName.toLowerCase() + "." + lastName.toLowerCase() + uniqueId + "@exemplu.com").replaceAll("[^a-z0-9.@]", "");
                    parent.setMotherEmail(motherEmail);
                    parent.setFatherEmail(fatherEmail);
                    parent.setEmail(motherEmail); // Email principal (pentru login)

                    // Verificăm dacă username-urile și email-urile sunt unice
                    if (usedUsernames.contains(parentUsername) || usedEmails.contains(motherEmail)) {
                        System.out.println("Username sau email deja folosite pentru părinte, se sare peste acest student: " + studentName);
                        continue;
                    }

                    if (usedUsernames.contains(studentUsername) || usedEmails.contains(studentEmail)) {
                        System.out.println("Username sau email deja folosite pentru student, se sare peste: " + studentName);
                        continue;
                    }

                    // Telefoane generate aleatoriu
                    parent.setMotherPhoneNumber("07" + String.format("%08d", random.nextInt(100000000)));
                    parent.setFatherPhoneNumber("07" + String.format("%08d", random.nextInt(100000000)));

                    // 👉 Salvăm întâi părintele în DB
                    Parent createdParent = (Parent) userService.createUser(parent);

                    // Marcăm username-ul și email-ul ca folosite
                    usedUsernames.add(parentUsername);
                    usedEmails.add(motherEmail);

                    // 👉 Asociem părintele la student
                    student.setParent(createdParent);

                    // 👉 Salvăm studentul în DB
                    Student createdStudent = (Student) userService.createUser(student);

                    // Marcăm username-ul și email-ul ca folosite
                    usedUsernames.add(studentUsername);
                    usedEmails.add(studentEmail);

                    // 👉 Abia acum creăm tokenurile pentru resetare parolă
                    PasswordResetToken studentToken = passwordResetService.createTokenForUser(createdStudent);
                    PasswordResetToken parentToken = passwordResetService.createTokenForUser(createdParent);

                    System.out.println("Elev creat: " + studentName + " (Clasa " + schoolClass.getName() + ") cu părinții " +
                            motherName + " și " + fatherName);

                    successfullyCreated++;

                } catch (Exception e) {
                    System.err.println("Eroare la crearea elevului și părintelui: " + e.getMessage());
                }
            }
        }

        System.out.println("Populare elevi și părinți finalizată! S-au creat " + successfullyCreated +
                " din " + totalStudentsToCreate + " elevi planificați.");
    }
}