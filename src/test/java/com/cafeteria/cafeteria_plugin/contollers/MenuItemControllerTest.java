package com.cafeteria.cafeteria_plugin.contollers;
import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.services.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import com.cafeteria.cafeteria_plugin.controllers.MenuItemController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuItemControllerTest {

    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController menuItemController;

    private MenuItem menuItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        menuItem = new MenuItem();
        menuItem.setId(1L);
        menuItem.setName("Pizza");
        menuItem.setDescription("Cheese Pizza");
        menuItem.setPrice(12.99);
        menuItem.setQuantity(50);
    }

    @Test
    public void testAddMenuItem() {
        // Adjusting the test to return String instead of MenuItem, since the controller method returns String.
        when(menuItemService.addMenuItem(any(MenuItem.class))).thenReturn(menuItem);

        // Since the controller is returning a ResponseEntity<String>
        ResponseEntity<String> response = menuItemController.addMenuItemWithImage("Pizza", "Cheese Pizza", 12.99, 50, null, null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Menu item added successfully!", response.getBody());
    }

    @Test
    public void testGetMenuItemById() {
        when(menuItemService.getMenuItemById(1L)).thenReturn(Optional.of(menuItem));

        ResponseEntity<MenuItem> response = menuItemController.getMenuItemById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(menuItem, response.getBody());
    }

    @Test
    public void testUpdateMenuItem() {
        MenuItem updatedMenuItem = new MenuItem();
        updatedMenuItem.setName("Veg Pizza");
        updatedMenuItem.setPrice(13.99);

        // Mock the service to return the existing menu item when fetching by ID
        when(menuItemService.getMenuItemById(1L)).thenReturn(Optional.of(menuItem));

        // Mock the update method to return the updated menu item
        when(menuItemService.updateMenuItem(1L, updatedMenuItem)).thenReturn(updatedMenuItem);

        // Call the controller method
        ResponseEntity<MenuItem> response = menuItemController.updateMenuItem(1L, updatedMenuItem);

        // Assert that the status is 200 (OK) and the updated fields are correct
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedMenuItem.getName(), response.getBody().getName());
        assertEquals(updatedMenuItem.getPrice(), response.getBody().getPrice());
    }


    @Test
    public void testDeleteMenuItem() {
        // Mock the deleteMenuItem to return true, indicating successful deletion
        when(menuItemService.deleteMenuItem(1L)).thenReturn(true);

        ResponseEntity<Void> response = menuItemController.deleteMenuItem(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

}
