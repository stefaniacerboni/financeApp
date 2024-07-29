package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryTest {
    @Test
    void testCreateCategory(){
        String name = "Travel";
        String description = "Category about travel";
        Category category = new Category(name, description);
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }
}
