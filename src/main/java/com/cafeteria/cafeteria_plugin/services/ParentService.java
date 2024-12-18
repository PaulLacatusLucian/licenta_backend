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

    public Parent addParent(Parent parent) {
        return parentRepository.save(parent);
    }

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public Optional<Parent> getParentById(Long id) {
        return parentRepository.findById(id);
    }

    public Parent updateParent(Long id, Parent updatedParent) {
        return parentRepository.findById(id)
                .map(existingParent -> {
                    existingParent.setName(updatedParent.getName());
                    existingParent.setPhoneNumber(updatedParent.getPhoneNumber());
                    existingParent.setEmail(updatedParent.getEmail());
                    return parentRepository.save(existingParent);
                }).orElseThrow(() -> new IllegalArgumentException("Parent not found"));
    }

    public void deleteParent(Long id) {
        parentRepository.deleteById(id);
    }
}
