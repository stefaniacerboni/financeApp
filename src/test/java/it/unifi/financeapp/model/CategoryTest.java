package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CategoryTest {

    @Test
    void testCreateCategory() {
        String name = "Travel";
        String description = "Category about travel";
        Category category = new Category(name, description);
        assertEquals(name, category.getName());
        assertEquals(description, category.getDescription());
    }

    @Test
    void testSetCategoryDetails() {
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
    void testEqualsWithDifferentName() {
        Category cat1 = new Category("Travel", "Expenses for travel");
        Category cat2 = new Category("Food", "Expenses for travel");
        assertNotEquals(cat1, cat2, "Categories should not be equal if names are different");
    }

    @Test
    void testEqualsWithDifferentDescription() {
        Category cat1 = new Category("Travel", "Expenses for travel");
        Category cat2 = new Category("Travel", "Expenses for business");
        assertNotEquals(cat1, cat2, "Categories should not be equal if descriptions are different");
    }

    @Test
    void testEqualsWithNullValuesInOne() {
        Category cat1 = new Category(null, null);
        Category cat2 = new Category("Travel", "Expenses for travel");
        assertNotEquals(cat1, cat2, "Categories should not be equal if one has null fields");
    }

    @Test
    void testEqualsWithNullValuesInBoth() {
        Category cat1 = new Category(null, null);
        Category cat2 = new Category(null, null);
        assertEquals(cat1, cat2, "Categories should be equal if both have all null fields");
    }

    @Test
    void testEqualsWithOneNullName() {
        Category cat1 = new Category(null, "Expenses for travel");
        Category cat2 = new Category("Travel", "Expenses for travel");
        assertNotEquals(cat1, cat2, "Categories should not be equal if one has a null name");
    }

    @Test
    void testEqualsWithOneNullDescription() {
        Category cat1 = new Category("Travel", null);
        Category cat2 = new Category("Travel", "Expenses for travel");
        assertNotEquals(cat1, cat2, "Categories should not be equal if one has a null description");
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

    @Test
    void testToString() {
        Category category = new Category("Travel", "Expenses for travel");
        String expected = "name='Travel', description='Expenses for travel'";
        assertEquals(expected, category.toString(), "The toString method should return the correct representation");
    }
}
