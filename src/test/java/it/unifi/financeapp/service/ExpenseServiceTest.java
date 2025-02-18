package it.unifi.financeapp.service;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.service.exceptions.InvalidExpenseException;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

	static Category category;
	static User user;
	@Mock
	private ExpenseRepository expenseRepository;
	@InjectMocks
	private ExpenseService expenseService;

	@BeforeAll
	static void setUp() {
		category = new Category("Food", "Category about food");
		user = new User("username", "name", "surname", "email");
	}

	@Nested
	@DisplayName("Happy Cases")
	class HappyCases {

		@Test
		void testAddExpense() {
			Expense expense = new Expense(category, user, 9.99, "2024-07-15");
			when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
			Expense result = expenseService.addExpense(expense);
			assertNotNull(result);
			assertEquals(category, result.getCategory());
			verify(expenseRepository).save(expense);
		}

		@Test
		void testSaveExistingExpense() {
			Expense existingExpense = new Expense(category, user, 9.99, "2024-07-15");
			existingExpense.setId(1L); // Simulate an existing expense
			when(expenseRepository.save(existingExpense)).thenReturn(existingExpense);

			Expense updatedExpense = expenseService.addExpense(existingExpense);

			assertNotNull(updatedExpense);
			assertEquals(existingExpense.getId(), updatedExpense.getId());
			verify(expenseRepository).save(existingExpense);
		}

		@Test
		void testAddExpenseOnBoundaryDate() {
			Expense boundaryExpense = new Expense(category, user, 50.00, "2024-12-31");
			when(expenseRepository.save(any(Expense.class))).thenReturn(boundaryExpense);

			Expense result = expenseService.addExpense(boundaryExpense);

			assertEquals("2024-12-31", result.getDate());
			verify(expenseRepository, times(1)).save(boundaryExpense);
		}

		@Test
		void testFindExpenseById() {
			Expense expectedExpense = new Expense(category, user, 20.00, "2024-07-15");
			when(expenseRepository.findById(expectedExpense.getId())).thenReturn(expectedExpense);

			Expense result = expenseService.findExpenseById(expectedExpense.getId());

			assertNotNull(result);
			assertEquals(expectedExpense, result);
			verify(expenseRepository).findById(expectedExpense.getId());
		}

		@Test
		void testUpdateExpense() {
			Expense originalExpense = new Expense(category, user, 300.00, "2024-07-16");
			Expense updatedExpense = new Expense(category, user, 350.00, "2024-07-16");

			when(expenseRepository.update(originalExpense)).thenReturn(updatedExpense);

			Expense result = expenseService.updateExpense(originalExpense);

			assertNotNull(result);
			assertEquals(updatedExpense.getAmount(), result.getAmount());
			verify(expenseRepository).update(originalExpense);
		}

		@Test
		void testGetAllExpenses() {
			List<Expense> expectedExpenses = Arrays.asList(
					new Expense(new Category("Food", "Category about food"),
							new User("username", "name", "surname", "email"), 9.99, "2024-07-15"),
					new Expense(new Category("Utilities", "Category about utilities"),
							new User("username", "name", "surname", "email"), 34.76, "2024-07-16"));

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

		@Test
		void testDeleteExpense() {
			Expense expense = new Expense(new Category("Food", "Category about food"),
					new User("username", "name", "surname", "email"), 9.99, "2024-07-15");

			when(expenseRepository.findById(expense.getId())).thenReturn(expense);

			expenseService.deleteExpense(expense.getId());

			verify(expenseRepository).delete(expense);
		}

		@Test
		void testDeleteAll() {
			expenseService.deleteAll();
			verify(expenseRepository).deleteAll();
		}
	}

	@Nested
	@DisplayName("Error Cases")
	class ErrorCases {

		@Test
		void testAddExpenseDatabaseError() {
			Expense newExpense = new Expense(category, user, 300.00, "2024-07-17");
			when(expenseRepository.save(any(Expense.class))).thenThrow(new RuntimeException("Database error"));

			Exception exception = assertThrows(RuntimeException.class, () -> expenseService.addExpense(newExpense));

			assertTrue(exception.getMessage().contains("Database error"));
		}

		@Test
		void testAddNullExpenseThrowsException() {
			assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense(null));
		}

		@Test
		void testAddExpenseWithInvalidCategory() {
			Expense invalidExpense = new Expense(null, user, 100.00, "2024-07-16");
			Exception exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(invalidExpense));
			assertEquals("Category cannot be null.", exception.getMessage());
		}

		@Test
		void testAddExpenseWithInvalidUser() {
			Expense invalidExpense = new Expense(category, null, 100.00, "2024-07-16");
			Exception exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(invalidExpense));
			assertEquals("User cannot be null.", exception.getMessage());
		}

		@Test
		void testAddExpenseWithInvalidAmount() {
			Expense expenseWithInvalidAmount = new Expense(category, user, -1, "2024-07-15");
			Exception exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithInvalidAmount));
			assertEquals("Amount must be greater than 0.", exception.getMessage());
			expenseWithInvalidAmount.setAmount(0);
			exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithInvalidAmount));
			assertEquals("Amount must be greater than 0.", exception.getMessage());

		}

		@Test
		void testAddExpenseWithEmptyDate() {
			Expense expenseWithEmptyDate = new Expense(category, user, 20, null);
			Exception exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithEmptyDate));
			assertEquals("Date is invalid.", exception.getMessage());
			// Also test for empty string
			expenseWithEmptyDate.setDate("");
			exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithEmptyDate));
			assertEquals("Date is invalid.", exception.getMessage());
		}

		@Test
		void testAddExpenseWithInvalidDate() {
			Expense expenseWithInvalidDate = new Expense(category, user, 20, "invalid");
			Exception exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithInvalidDate));
			assertEquals("Date is invalid.", exception.getMessage());
			// Also test for dd-mm-yyyy string date
			expenseWithInvalidDate.setDate("11-01-2021");
			exception = assertThrows(InvalidExpenseException.class,
					() -> expenseService.addExpense(expenseWithInvalidDate));
			assertEquals("Date is invalid.", exception.getMessage());
		}

		@Test
		void testIsValidDateWithNull() {
			assertFalse(expenseService.isValidDate(null));
		}

		@Test
		void testIsValidDateWithEmptyString() {
			assertFalse(expenseService.isValidDate(""));
		}

		@Test
		void testAddExpenseThrowsServiceException() {
			Expense expense = new Expense(category, user, 299.99, "2024-08-01");
			doThrow(new PersistenceException("Database unavailable")).when(expenseRepository).save(expense);
			ServiceException thrown = assertThrows(ServiceException.class, () -> expenseService.addExpense(expense),
					"ServiceException should be thrown");
			assertTrue(thrown.getMessage().contains("Error while adding expense"),
					"Exception message should indicate problem with adding expense");
			assertNotNull(thrown.getCause(), "ServiceException should have a cause");
			assertEquals(PersistenceException.class, thrown.getCause().getClass(),
					"The cause of ServiceException should be a PersistenceException");
			verify(expenseRepository).save(expense);
		}

		@Test
		void testGetAllExpensesWithError() {
			when(expenseRepository.findAll()).thenThrow(new RuntimeException("Database error"));
			assertThrows(RuntimeException.class, () -> expenseService.getAllExpenses());
		}

		@Test
		void testUpdateExpenseWithInvalidData() {
			Expense invalidExpense = new Expense(category, user, 300.00, null);
			assertThrows(InvalidExpenseException.class, () -> expenseService.updateExpense(invalidExpense));
		}

		@Test
		void testDeleteNonExistentExpense() {
			Long expenseId = 1L;
			when(expenseRepository.findById(expenseId)).thenReturn(null);
			assertThrows(IllegalArgumentException.class, () -> expenseService.deleteExpense(expenseId));
			verify(expenseRepository).findById(expenseId);
			verify(expenseRepository, never()).delete(any(Expense.class));
		}

		@Test
		void testDeleteExpenseNull() {
			Exception exception = assertThrows(IllegalArgumentException.class, () -> expenseService.deleteExpense(1L));
			assertEquals("Cannot delete a null expense.", exception.getMessage());
		}

	}
}