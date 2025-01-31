package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chefs")
public class ChefController {

    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    // ✅ Înregistrare bucătăreasă
    @PostMapping("/register")
    public ResponseEntity<Chef> registerChef(@RequestBody Chef chef) {
        Chef savedChef = chefService.createChef(chef);
        return ResponseEntity.ok(savedChef);
    }

    // ✅ Obținerea tuturor bucătăreselor
    @GetMapping("/all")
    public ResponseEntity<List<Chef>> getAllChefs() {
        List<Chef> chefs = chefService.getAllChefs();
        return ResponseEntity.ok(chefs);
    }

    // ✅ Obținerea unei bucătărese după ID
    @GetMapping("/{id}")
    public ResponseEntity<Chef> getChefById(@PathVariable Long id) {
        Optional<Chef> chef = chefService.getChefById(id);
        return chef.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Ștergerea unei bucătărese după ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChef(@PathVariable Long id) {
        boolean deleted = chefService.deleteChef(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
