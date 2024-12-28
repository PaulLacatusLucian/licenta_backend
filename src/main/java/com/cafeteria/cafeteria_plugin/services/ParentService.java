package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Optional<Parent> getParentById(Long id) {
        return parentRepository.findById(id);
    }

    public void deleteParent(Long id) {
        if (!parentRepository.existsById(id)) {
            throw new IllegalArgumentException("Părintele cu ID-ul " + id + " nu există.");
        }
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
