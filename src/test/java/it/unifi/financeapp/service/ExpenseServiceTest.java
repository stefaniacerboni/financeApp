package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.service.exceptions.InvalidExpenseException;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.PersistenceException;
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


        @Test
        void testGetAllExpensesEmptyList() {
            when(expenseRepository.findAll()).thenReturn(List.of());

            List<Expense> actualExpenses = expenseService.getAllExpenses();

            assertNotNull(actualExpenses);
            assertTrue(actualExpenses.isEmpty());
            verify(expenseRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Error Cases")
    class ErrorCases {
        @Test
        void testAddExpenseWithInvalidData() {
            Expense invalidExpense = new Expense(null, null, -100.00, "2024-07-16");

            assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(invalidExpense));
        }

        @Test
        void testAddNullExpenseThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense(null));
        }

        @Test
        void testAddExpenseWithInvalidAmount() {
            Category category = new Category("Food", "Category about food");
            User user = new User("username", "name", "surname", "email");
            Expense expenseWithInvalidAmount = new Expense(category, user, -1, "2024-07-15");

            Exception exception = assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(expenseWithInvalidAmount));

            assertEquals("Amount must be greater than 0.", exception.getMessage());

            expenseWithInvalidAmount.setAmount(0);

            exception = assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(expenseWithInvalidAmount));

            assertEquals("Amount must be greater than 0.", exception.getMessage());

        }

        @Test
        void testAddExpenseWithEmptyDate() {
            Category category = new Category("Food", "Category about food");
            User user = new User("username", "name", "surname", "email");
            Expense expenseWithEmptyDate = new Expense(category, user, 20, null);

            Exception exception = assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(expenseWithEmptyDate));

            assertEquals("Date cannot be empty.", exception.getMessage());

            // Also test for empty string
            expenseWithEmptyDate.setDate("");
            exception = assertThrows(InvalidExpenseException.class, () -> expenseService.addExpense(expenseWithEmptyDate));

            assertEquals("Date cannot be empty.", exception.getMessage());
        }

        @Test
        void testAddExpenseDatabaseError() {
            Category category = new Category("Accommodation", "Category about accommodation");
            User user = new User("username", "name", "surname", "email");
            Expense newExpense = new Expense(category, user, 300.00, "2024-07-17");
            when(expenseRepository.save(any(Expense.class))).thenThrow(new RuntimeException("Database error"));

            Exception exception = assertThrows(RuntimeException.class, () -> expenseService.addExpense(newExpense));

            assertTrue(exception.getMessage().contains("Database error"));
        }


        @Test
        void testAddExpenseThrowsServiceException() {
            // Create an Expense object to use in the test
            Expense expense = new Expense(
                    new Category("Travel", "Business trip"),
                    new User("username", "name", "surname", "email"),
                    299.99, "2024-08-01");

            // Setup the mock to throw PersistenceException when save is called
            doThrow(new PersistenceException("Database unavailable")).when(expenseRepository).save(expense);

            // Assert that ServiceException is thrown when addExpense is called
            ServiceException thrown = assertThrows(ServiceException.class, () -> expenseService.addExpense(expense), "ServiceException should be thrown");

            // Verify the message of the thrown ServiceException
            assertTrue(thrown.getMessage().contains("Error while adding expense"), "Exception message should indicate problem with adding expense");
            assertNotNull(thrown.getCause(), "ServiceException should have a cause");
            assertEquals(PersistenceException.class, thrown.getCause().getClass(), "The cause of ServiceException should be a PersistenceException");

            // Ensure that save was attempted on the repository
            verify(expenseRepository).save(expense);
        }

        @Test
        void testUpdateExpenseWithInvalidData() {
            Category category = new Category("Travel", "Category about travel");
            User user = new User("username", "name", "surname", "email");
            Expense invalidExpense = new Expense(category, user, 300.00, null);
            assertThrows(InvalidExpenseException.class, () -> expenseService.updateExpense(invalidExpense));
        }

        @Test
        void testDeleteNonExistentExpense() {
            Long expenseId = 1L;

            // Assuming findById will return null indicating no expense found
            when(expenseRepository.findById(expenseId)).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> expenseService.deleteExpense(expenseId));

            // Verify findById was called
            verify(expenseRepository).findById(expenseId);

            // Verify delete was never called since no expense was found
            verify(expenseRepository, never()).delete(any(Expense.class));
        }

    }
}
