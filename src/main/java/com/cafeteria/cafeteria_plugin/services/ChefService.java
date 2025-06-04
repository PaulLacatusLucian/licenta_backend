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

    //Neuer Koch wird erstellt
    public Chef createChef(Chef chef) {
        if (chefRepository.existsByEmail(chef.getEmail())) {
            throw new IllegalArgumentException("Die E-Mail-Adresse wird bereits verwendet!");
        }
        if (chefRepository.existsByUsername(chef.getUsername())) {
            throw new IllegalArgumentException("Der Benutzername wird bereits verwendet!");
        }
        return chefRepository.save(chef);
    }

    //Alle Köche abrufen
    public List<Chef> getAllChefs() {
        return chefRepository.findAll();
    }

    //Koch nach ID abrufen
    public Optional<Chef> getChefById(Long id) {
        return chefRepository.findById(id);
    }

    //Koch nach ID löschen
    public boolean deleteChef(Long id) {
        if (!chefRepository.existsById(id)) {
            throw new IllegalArgumentException("Koch mit der ID " + id + " existiert nicht!");
        }
        chefRepository.deleteById(id);
        return true;
    }

    //Koch aktualisieren
    public Chef updateChef(Long id, Chef chefDetails) {
        return chefRepository.findById(id).map(existingChef -> {
            if (chefDetails.getName() != null) {
                existingChef.setName(chefDetails.getName());
            }

            // 🔐 Benutzername und Passwort werden nur aktualisiert, wenn sie angegeben wurden
            if (chefDetails.getUsername() != null) {
                existingChef.setUsername(chefDetails.getUsername());
            }

            if (chefDetails.getPassword() != null) {
                existingChef.setPassword(chefDetails.getPassword());
            }

            return chefRepository.save(existingChef);
        }).orElseThrow(() -> new IllegalArgumentException("Koch mit der ID " + id + " wurde nicht gefunden."));
    }
}
