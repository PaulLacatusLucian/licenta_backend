package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.email.PasswordResetTokenRepository;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Obține toți părinții
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    // ✅ Obține un părinte după ID
    public Parent getParentById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Părintele cu ID-ul " + id + " nu a fost găsit."));
    }

    // ✅ Adaugă un părinte în baza de date
    @Transactional
    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found with ID: " + id));

        tokenRepository.deleteAllByUser_Id(parent.getId());

        parentRepository.delete(parent);

        userRepository.deleteById(parent.getId());
    }


    // ✅ Actualizează datele unui părinte
    @Transactional
    public Parent updateParent(Long id, Parent updatedParent) {
        Parent existingParent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Părintele cu ID-ul " + id + " nu a fost găsit."));

        existingParent.setMotherName(updatedParent.getMotherName());
        existingParent.setMotherEmail(updatedParent.getMotherEmail());
        existingParent.setMotherPhoneNumber(updatedParent.getMotherPhoneNumber());
        existingParent.setFatherName(updatedParent.getFatherName());
        existingParent.setFatherEmail(updatedParent.getFatherEmail());
        existingParent.setFatherPhoneNumber(updatedParent.getFatherPhoneNumber());

        return parentRepository.save(existingParent);
    }

    @Transactional
    public Student addStudentToParent(Long parentId, Student student) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Părintele nu există"));

        student.setParent(parent);
        return studentRepository.save(student);
    }

    public List<String> getParentEmailsByClassId(Long classId) {
        List<Parent> parents = parentRepository.findDistinctByStudents_StudentClass_Id(classId);

        return parents.stream()
                .flatMap(parent -> Stream.of(parent.getMotherEmail(), parent.getFatherEmail()))
                .filter(email -> email != null && !email.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    public Parent findByUsername(String username) {
        return parentRepository.findByUsername(username);
    }
}
