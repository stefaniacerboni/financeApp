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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        @Test
        public void testSaveExistingExpense() {
            // Arrange
            Category existingCategory = new Category("name", "description");
            User existingUser = new User("username", "name", "surname", "email");
            Expense existingExpense = new Expense(existingCategory, existingUser, 9.99, "2024-07-15");
            existingExpense.setId(1L); // Simulate an existing expense
            when(expenseRepository.save(existingExpense)).thenReturn(existingExpense);

            // Act
            Expense updatedExpense = expenseService.addExpense(existingExpense);

            // Assert
            assertNotNull(updatedExpense);
            assertEquals(existingExpense.getId(), updatedExpense.getId());
            verify(expenseRepository).save(existingExpense);
        }

        @Test
        void testAddExpenseOnBoundaryDate() {
            Category category = new Category("Food", "Category about food");
            User user = new User("username", "name", "surname", "email");
            Expense boundaryExpense = new Expense(category, user, 50.00, "2024-12-31");
            when(expenseRepository.save(any(Expense.class))).thenReturn(boundaryExpense);

            Expense result = expenseService.addExpense(boundaryExpense);

            assertEquals("2024-12-31", result.getDate());
            verify(expenseRepository, times(1)).save(boundaryExpense);
        }

        @Test
        void testFindExpenseById() {
            Category category = new Category("Food", "Category about food");
            User user = new User("username", "name", "surname", "email");
            Expense expectedExpense = new Expense(category, user, 20.00, "2024-07-15");
            when(expenseRepository.findById(expectedExpense.getId())).thenReturn(expectedExpense);

            Expense result = expenseService.findExpenseById(expectedExpense.getId());

            assertNotNull(result);
            assertEquals(expectedExpense, result);
            verify(expenseRepository).findById(expectedExpense.getId());
        }


        @Test
        void testUpdateExpense() {
            Category category = new Category("Travel", "Category about travel");
            User user = new User("username", "name", "surname", "email");
            Expense originalExpense = new Expense(category, user, 300.00, "2024-07-16");
            Expense updatedExpense = new Expense(category, user, 350.00, "2024-07-16");

            when(expenseRepository.update(originalExpense)).thenReturn(updatedExpense);

            Expense result = expenseService.updateExpense(originalExpense);

            assertNotNull(result);
            assertEquals(updatedExpense.getAmount(), result.getAmount());
            verify(expenseRepository).update(originalExpense);
        }


        @Test
        void testDeleteExpense() {
            Expense expense = new Expense(new Category("Food", "Category about food"),
                    new User("username", "name", "surname", "email"),
                    9.99, "2024-07-15");

            when(expenseRepository.findById(expense.getId())).thenReturn(expense);

            expenseService.deleteExpense(expense.getId());

            verify(expenseRepository).delete(expense);
        }

        @Test
        void testDeleteExpenseNull() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> expenseService.deleteExpense(1L));
            assertEquals(exception.getMessage(), "Cannot delete a null expense.");
        }


        @Test
        void testGetAllExpenses() {
            List<Expense> expectedExpenses = Arrays.asList(
                    new Expense(new Category("Food", "Category about food"),
                            new User("username", "name", "surname", "email"),
                            9.99, "2024-07-15"),
                    new Expense(new Category("Utilities", "Category about utilities"),
                            new User("username", "name", "surname", "email"),
                            34.76, "2024-07-16")
            );

            when(expenseRepository.findAll()).thenReturn(expectedExpenses);

            List<Expense> actualExpenses = expenseService.getAllExpenses();

            assertNotNull(actualExpenses);
            assertEquals(2, actualExpenses.size());
            assertEquals(expectedExpenses, actualExpenses);
            verify(expenseRepository).findAll();
        }

    }
}
