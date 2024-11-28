package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.repositories.MenuItemRepository;
import com.cafeteria.cafeteria_plugin.repositories.OrderHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, OrderHistoryRepository orderHistoryRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
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

    public MenuItem purchaseMenuItem(Long id, int quantity) {
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));
        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        orderHistoryRepository.save(order);

        return menuItem;
    }

    public List<OrderHistory> getOrderHistoryForMonth(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByOrderTimeBetween(start, end);
    }

    public double calculateTotalForMonth(int month, int year) {
        List<OrderHistory> orders = getOrderHistoryForMonth(month, year);
        return orders.stream().mapToDouble(OrderHistory::getPrice).sum();
    }
}
