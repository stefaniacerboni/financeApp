package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CategoryTest {
    @Test
    void testCreateCategory(){
        String name = "Travel";
        String description = "Category about travel";
        Category category = new Category(name, description);
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void testSetCategoryDetails(){
        Category category = new Category();
        String name = "Name";
        String description = "Description";
        category.setName(name);
        category.setDescription(description);
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void testEqualsWithSelf() {
        Category category = new Category("Travel", "Expenses for travel");
        assertEquals(category, category);
    }

    @Test
    void testEqualsWithSameData() {
        Category cat1 = new Category("Travel", "Expenses for travel");
        Category cat2 = new Category("Travel", "Expenses for travel");
        assertEquals(cat1, cat2);
    }

    @Test
    void testEqualsWithDifferentData() {
        Category cat1 = new Category("Travel", "Expenses for travel");
        Category cat2 = new Category("Food", "Expenses for food");
        assertNotEquals(cat1, cat2);
    }

    @Test
    void testEqualsAgainstNull() {
        Category category = new Category("Travel", "Expenses for travel");
        assertNotEquals(category, null);
    }

    @Test
    void testEqualsAgainstDifferentClass() {
        Category category = new Category("Travel", "Expenses for travel");
        Object other = new Object();
        assertNotEquals(category, other);
    }

    @Test
    void testHashCodeConsistency() {
        Category category = new Category("Travel", "Expenses for travel");
        int hashCode1 = category.hashCode();
        int hashCode2 = category.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEqualObjectsSameHashCode() {
        Category cat1 = new Category("Travel", "Expenses for travel");
        Category cat2 = new Category("Travel", "Expenses for travel");
        assertEquals(cat1.hashCode(), cat2.hashCode());
    }
}
