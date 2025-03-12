package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

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

    // ✅ Șterge un părinte și studenții asociați
    @Transactional
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Părintele cu ID-ul " + id + " nu există."));

        // Șterge studenții asociați
        studentRepository.deleteByParentId(id);

        // Șterge părintele
        parentRepository.delete(parent);
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
}
