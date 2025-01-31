package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.MenuItemRepository;
import com.cafeteria.cafeteria_plugin.repositories.OrderHistoryRepository;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           OrderHistoryRepository orderHistoryRepository,
                           ParentRepository parentRepository,
                           StudentRepository studentRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    // ✅ Adaugă un nou produs în meniu
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // ✅ Returnează toate produsele din meniu
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // ✅ Returnează un produs după ID
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    // ✅ Actualizează un produs
    public MenuItem updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setQuantity(updatedMenuItem.getQuantity()); // Actualizare cantitate
            return menuItemRepository.save(existingMenuItem);
        }).orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));
    }

    // ✅ Șterge un produs
    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ Actualizează imaginea unui produs
    public void updateMenuItemImage(Long id, String imageUrl) {
        menuItemRepository.findById(id).ifPresent(menuItem -> {
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
        });
    }

    // ✅ Părintele comandă pentru elev
    public void purchaseMenuItem(Long parentId, Long studentId, Long menuItemId, int quantity) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));

        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available.");
        }

        // Scade cantitatea din stoc
        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        // Creează comanda și leag-o de părinte și elev
        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        order.setParent(parent);
        order.setStudent(student);

        orderHistoryRepository.save(order);
    }

    // ✅ Returnează istoricul comenzilor unui elev
    public List<OrderHistory> getOrderHistoryForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return orderHistoryRepository.findByStudentId(student.getId());
    }

    // ✅ Returnează istoricul comenzilor unui părinte pentru un elev
    public List<OrderHistory> getOrderHistoryForParent(Long parentId, Long studentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return orderHistoryRepository.findByParentIdAndStudentId(parent.getId(), student.getId());
    }

    // ✅ Generează o factură pentru un elev
    public String generateInvoiceForStudent(Long studentId, int month, int year) {
        List<OrderHistory> orders = getOrderHistoryForStudent(studentId);
        double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder invoice = new StringBuilder();
        invoice.append("Invoice for ").append(month).append("/").append(year).append("\n");
        invoice.append("====================================\n");
        for (OrderHistory order : orders) {
            invoice.append(order.getMenuItemName())
                    .append(" x ").append(order.getQuantity())
                    .append(" = $").append(order.getPrice())
                    .append(" | Date: ").append(order.getOrderTime().format(dateTimeFormatter))
                    .append("\n");
        }
        invoice.append("====================================\n");
        invoice.append("Total: $").append(total).append("\n");

        return invoice.toString();
    }
}
