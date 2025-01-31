package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.repositories.ChefRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChefService {

    private final ChefRepository chefRepository;

    public ChefService(ChefRepository chefRepository) {
        this.chefRepository = chefRepository;
    }

    // ✅ Creare bucătăreasă
    public Chef createChef(Chef chef) {
        return chefRepository.save(chef);
    }

    // ✅ Obținere toate bucătăresele
    public List<Chef> getAllChefs() {
        return chefRepository.findAll();
    }

    // ✅ Obținere bucătăreasă după ID
    public Optional<Chef> getChefById(Long id) {
        return chefRepository.findById(id);
    }

    // ✅ Ștergere bucătăreasă după ID
    public boolean deleteChef(Long id) {
        if (chefRepository.existsById(id)) {
            chefRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
