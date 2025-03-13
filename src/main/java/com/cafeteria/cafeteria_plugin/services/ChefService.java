package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.repositories.ChefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChefService {

    private final ChefRepository chefRepository;

    @Autowired
    public ChefService(ChefRepository chefRepository) {
        this.chefRepository = chefRepository;
    }

    // ✅ Creare bucătar
    public Chef createChef(Chef chef) {
        if (chefRepository.existsByEmail(chef.getEmail())) {
            throw new IllegalArgumentException("Email-ul este deja folosit!");
        }
        if (chefRepository.existsByUsername(chef.getUsername())) {
            throw new IllegalArgumentException("Username-ul este deja folosit!");
        }
        return chefRepository.save(chef);
    }

    // ✅ Obținere toți bucătarii
    public List<Chef> getAllChefs() {
        return chefRepository.findAll();
    }

    // ✅ Obținere bucătar după ID
    public Optional<Chef> getChefById(Long id) {
        return chefRepository.findById(id);
    }

    // ✅ Ștergere bucătar după ID
    public boolean deleteChef(Long id) {
        if (!chefRepository.existsById(id)) {
            throw new IllegalArgumentException("Chef-ul cu ID-ul " + id + " nu există!");
        }
        chefRepository.deleteById(id);
        return true;
    }

    // ✅ Actualizare bucătar
    public Chef updateChef(Long id, Chef chefDetails) {
        return chefRepository.findById(id).map(existingChef -> {
            if (chefDetails.getName() != null) {
                existingChef.setName(chefDetails.getName());
            }

            // 🔐 Nu actualizăm username și password dacă nu sunt furnizate
            if (chefDetails.getUsername() != null) {
                existingChef.setUsername(chefDetails.getUsername());
            }

            if (chefDetails.getPassword() != null) {
                existingChef.setPassword(chefDetails.getPassword());
            }

            return chefRepository.save(existingChef);
        }).orElseThrow(() -> new IllegalArgumentException("Chef cu ID-ul " + id + " nu a fost găsit."));
    }
}
