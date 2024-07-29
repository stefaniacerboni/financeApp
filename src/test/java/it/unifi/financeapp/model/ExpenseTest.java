package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExpenseTest {

    @Test
    void testExpenseCreation() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        double amount = 100.0;
        String date = "2024-12-12";
        Expense expense = new Expense(category, user, amount, date);
        assertEquals(category, expense.getCategory());
        assertEquals(user, expense.getUser());
        assertEquals(amount, expense.getAmount());
        assertEquals(date, expense.getDate());
    }

    @Test
    void testSetExpenseDetails() {
        Expense expense = new Expense();
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        double amount = 100.0;
        String date = "2024-12-12";
        expense.setCategory(category);
        expense.setUser(user);
        expense.setAmount(amount);
        expense.setDate(date);
        assertEquals(category, expense.getCategory());
        assertEquals(user, expense.getUser());
        assertEquals(amount, expense.getAmount());
        assertEquals(date, expense.getDate());
    }

    @Test
    void testEqualsWithSelf() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense = new Expense(category, user, 100L, "2024-12-23");
        assertEquals(expense, expense);
    }

    @Test
    void testEqualsWithSameData() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense1 = new Expense(category, user, 100L, "2024-12-23");
        Expense expense2 = new Expense(category, user, 100L, "2024-12-23");
        assertEquals(expense1, expense2);
    }

    @Test
    void testEqualsWithDifferentData() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense1 = new Expense(category, user, 100L, "2024-12-23");
        Expense expense2 = new Expense(category, user, 150L, "2024-09-23");
        assertNotEquals(expense1, expense2);
    }

    @Test
    void testEqualsAgainstNull() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense = new Expense(category, user, 100L, "2024-12-23");
        assertNotEquals(expense, null);
    }

    @Test
    void testEqualsAgainstDifferentClass() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense = new Expense(category, user, 100L, "2024-12-23");
        Object other = new Object();
        assertNotEquals(expense, other);
    }

    @Test
    void testHashCodeConsistency() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense = new Expense(category, user, 100L, "2024-12-23");
        int hashCode1 = expense.hashCode();
        int hashCode2 = expense.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testEqualObjectsSameHashCode() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Expense expense1 = new Expense(category, user, 100L, "2024-12-23");
        Expense expense2 = new Expense(category, user, 100L, "2024-12-23");
        assertEquals(expense1.hashCode(), expense2.hashCode());
    }
}
