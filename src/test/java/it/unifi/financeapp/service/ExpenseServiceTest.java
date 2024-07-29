package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.ExpenseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Nested
    @DisplayName("Happy Cases")
    class HappyCases {

        @Test
        void testAddExpense() {
            Category category = new Category("Food", "Category about food");
            User user = new User("username", "name", "surname", "email");
            Expense expense = new Expense(category, user, 9.99, "2024-07-15");
            when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

            Expense result = expenseService.addExpense(expense);

            assertNotNull(result);
            assertEquals(category, result.getCategory());
            verify(expenseRepository).save(expense);
        }
    }
}
