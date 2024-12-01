package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.services.MenuItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Value("${image.upload.dir}")
    private String uploadDir;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMenuItemWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "allergens", required = false) List<String> allergens, // Lista de alergeni
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadDir + fileName);
                file.transferTo(destinationFile);
                imageUrl = "/images/" + fileName;
            }

            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setQuantity(quantity);
            menuItem.setImageUrl(imageUrl);
            menuItem.setAllergens(allergens); // Setăm lista de alergeni

            menuItemService.addMenuItem(menuItem);

            return ResponseEntity.ok("Menu item added successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }



    @GetMapping("/all")
    public List<MenuItem> getAllMenuItems() {
        return menuItemService.getAllMenuItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> menuItem = menuItemService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedMenuItem) {
        Optional<MenuItem> existingMenuItem = menuItemService.getMenuItemById(id);
        if (existingMenuItem.isPresent()) {
            MenuItem updatedItem = menuItemService.updateMenuItem(id, updatedMenuItem);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id) {
        boolean isDeleted = menuItemService.deleteMenuItem(id);
        if (isDeleted) {
            return ResponseEntity.ok("MenuItem deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MenuItem not found");
        }
    }

    @PostMapping("/{menuItemId}/purchase")
    public ResponseEntity<String> purchaseMenuItem(
            @RequestParam Long userId, // ID-ul utilizatorului
            @PathVariable Long menuItemId, // ID-ul produsului
            @RequestParam int quantity) {
        try {
            menuItemService.purchaseMenuItem(userId, menuItemId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/{userId}/history/{month}/{year}")
    public ResponseEntity<List<OrderHistory>> getUserOrderHistory(@PathVariable Long userId, @PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(menuItemService.getOrderHistoryForUser(userId, month, year));
    }

    @GetMapping("/{userId}/invoice/{month}/{year}")
    public ResponseEntity<String> generateUserInvoice(@PathVariable Long userId, @PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(menuItemService.generateInvoiceForUser(userId, month, year));
    }

    @GetMapping("/{userId}/invoice/{month}/{year}/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long userId,
            @PathVariable int month,
            @PathVariable int year) {
        String invoiceContent = menuItemService.generateInvoiceForUser(userId, month, year);
        byte[] pdfBytes = menuItemService.generateInvoicePdf(invoiceContent);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=invoice_" + userId + "_" + month + "_" + year + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds the maximum limit of 10MB");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir + fileName);
            file.transferTo(destinationFile);

            String imageUrl = "/images/" + fileName;
            menuItemService.updateMenuItemImage(id, imageUrl);

            return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}
