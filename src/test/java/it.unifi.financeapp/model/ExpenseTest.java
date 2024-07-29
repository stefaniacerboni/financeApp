package it.unifi.financeapp.model;

import org.junit.jupiter.api.Test;

public class ExpenseTest {
    @Test
    void testExpenseCreation(){
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("Username", "Name", "Surname", "Email");
        Long amount = 100L;
        String date = "2024-12-12";
        Expense expense = new Expense(category, user, amount, date);
    }
}
