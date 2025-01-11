package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public ParentService(ParentRepository parentRepository, StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Optional<Parent> getParentById(Long id) {
        return parentRepository.findById(id);
    }

    @Transactional
    public void deleteParent(Long id) {
        if (!parentRepository.existsById(id)) {
            throw new IllegalArgumentException("Părintele cu ID-ul " + id + " nu există.");
        }

        // Șterge studenții asociați cu părintele
        studentRepository.deleteByParentId(id);

        // Șterge părintele
        parentRepository.deleteById(id);
    }

    public Parent updateParent(Long id, Parent updatedParent) {
        return parentRepository.findById(id)
                .map(existingParent -> {
                    existingParent.setMotherName(updatedParent.getMotherName());
                    existingParent.setMotherEmail(updatedParent.getMotherEmail());
                    existingParent.setMotherPhoneNumber(updatedParent.getMotherPhoneNumber());
                    existingParent.setFatherName(updatedParent.getFatherName());
                    existingParent.setFatherEmail(updatedParent.getFatherEmail());
                    existingParent.setFatherPhoneNumber(updatedParent.getFatherPhoneNumber());
                    return parentRepository.save(existingParent);
                })
                .orElseThrow(() -> new IllegalArgumentException("Părintele cu ID-ul " + id + " nu a fost găsit."));
    }
}
