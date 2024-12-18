package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.User;
import com.cafeteria.cafeteria_plugin.repositories.MenuItemRepository;
import com.cafeteria.cafeteria_plugin.repositories.OrderHistoryRepository;
import com.cafeteria.cafeteria_plugin.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           OrderHistoryRepository orderHistoryRepository,
                           UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.userRepository = userRepository;
    }


    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    public MenuItem updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setQuantity(updatedMenuItem.getQuantity()); // Actualizare cantitate
            return menuItemRepository.save(existingMenuItem);
        }).orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));
    }

    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public void updateMenuItemImage(Long id, String imageUrl) {
        menuItemRepository.findById(id).ifPresent(menuItem -> {
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
        });
    }

    public MenuItem purchaseMenuItem(Long userId, Long menuItemId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));

        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Scade cantitatea din stoc
        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        // Creează o comandă și leag-o de utilizator
        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        order.setUser(user); // Asociază comanda utilizatorului
        orderHistoryRepository.save(order);

        return menuItem;
    }


    public List<OrderHistory> getOrderHistoryForUser(Long userId, int month, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByUserAndOrderTimeBetween(user, start, end);
    }


    public String generateInvoiceForUser(Long userId, int month, int year) {
        List<OrderHistory> orders = getOrderHistoryForUser(userId, month, year);
        double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder invoice = new StringBuilder();
        invoice.append("Invoice for ").append(month).append("/").append(year).append("\n");
        invoice.append("====================================\n");
        for (OrderHistory order : orders) {
            invoice.append(order.getMenuItemName())
                    .append(" x ").append(order.getQuantity())
                    .append(" = $").append(order.getPrice())
                    .append(" | Date: ").append(order.getOrderTime().format(dateTimeFormatter)) // Adaugă data și ora
                    .append("\n");
        }
        invoice.append("====================================\n");
        invoice.append("Total: $").append(total).append("\n");

        return invoice.toString();
    }

}
