package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ExpenseRepositoryTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;
    @Mock
    private TypedQuery<Expense> expenseQuery;
    @Mock
    private NativeQuery<User> nativeQuery;

    private ExpenseRepositoryImpl expenseRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(entityManager.getTransaction()).thenReturn(transaction);
        expenseRepository = new ExpenseRepositoryImpl(entityManager);
    }

    @Test
    void testSaveNewExpense() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("john.doe", "john.doe@example.com");
        Expense newExpense = new Expense(category, user, 100.0, "2021-07-16");
        when(entityManager.merge(user)).thenReturn(user);
        when(entityManager.merge(category)).thenReturn(category);

        expenseRepository.save(newExpense);

        verify(entityManager).persist(newExpense);
        verify(transaction).begin();
        verify(entityManager).merge(user);
        verify(entityManager).merge(category);
        assertEquals(user, newExpense.getUser());
        assertEquals(category, newExpense.getCategory());
        verify(transaction).commit();
    }

    @Test
    void testFindById() {
        Long expenseId = 1L;
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("john.doe", "john.doe@example.com");
        Expense expectedExpense = new Expense(category, user, 100.0, "2021-07-16");

        when(entityManager.find(Expense.class, expenseId)).thenReturn(expectedExpense);

        Expense foundExpense = expenseRepository.findById(expenseId);

        assertNotNull(foundExpense);
        assertEquals(expectedExpense, foundExpense);
        verify(entityManager).find(Expense.class, expenseId);
    }

    @Test
    void testFindAllExpenses() {
        Category category = new Category("Travel", "Expenses for travel");
        User user = new User("john.doe", "john.doe@example.com");
        Expense expense1 = new Expense(category, user, 100.0, "2021-07-16");
        Expense expense2 = new Expense(category, user, 150.0, "2021-07-17");

        when(entityManager.createQuery("SELECT e FROM Expense e", Expense.class)).thenReturn(expenseQuery);
        when(expenseQuery.getResultList()).thenReturn(Arrays.asList(expense1, expense2));

        List<Expense> expenses = expenseRepository.findAll();

        assertNotNull(expenses);
        assertEquals(2, expenses.size());
        verify(entityManager).createQuery("SELECT e FROM Expense e", Expense.class);
    }

    @Test
    void testUpdateExpense() {
        Category category = new Category("Meals", "Meals and Entertainment");
        User user = new User("jane.doe", "jane@example.com");
        Expense existingExpense = new Expense(category, user, 100.00, "2022-01-02");
        when(entityManager.merge(user)).thenReturn(user);
        when(entityManager.merge(category)).thenReturn(category);
        when(entityManager.merge(existingExpense)).thenReturn(existingExpense);
        Expense updatedExpense = expenseRepository.update(existingExpense);
        assertEquals(existingExpense, updatedExpense);
        verify(entityManager).merge(existingExpense);
        verify(transaction).begin();
        verify(entityManager).merge(user);
        verify(entityManager).merge(category);
        assertEquals(user, updatedExpense.getUser());
        assertEquals(category, updatedExpense.getCategory());
        verify(transaction).commit();
    }

    @Test
    void testManageDependencies() {
        Category category = new Category("Meals", "Meals and Entertainment");
        User user = new User("jane.doe", "jane@example.com");
        Expense expense = spy(new Expense(category, user, 100.00, "2022-01-02"));
        when(entityManager.merge(user)).thenReturn(user);
        when(entityManager.merge(category)).thenReturn(category);

        expenseRepository.manageDependencies(expense);

        verify(expense).setUser(user);
        verify(expense).setCategory(category);
    }

    @Test
    void testDeleteExpense() {
        Expense expenseToDelete = new Expense(new Category("Accommodation", "Hotel expenses"), new User("john.doe", "john@example.com"), 300.00, "2022-01-03");
        expenseRepository.delete(expenseToDelete);
        verify(entityManager).remove(expenseToDelete);
        verify(transaction).begin();
        verify(transaction).commit();
    }

    @Test
    void testSaveExistingExpense() {
        Expense existingExpense = new Expense(new Category("Meals", "Meals and Entertainment"), new User("john.doe", "john@example.com"), 150.00, "2022-01-02");
        existingExpense.setId(1L); // Assume this expense already has an ID, indicating it is already persisted
        when(entityManager.merge(existingExpense)).thenReturn(existingExpense);

        Expense updatedExpense = expenseRepository.save(existingExpense);

        verify(entityManager).merge(existingExpense);
        verify(transaction).begin();
        verify(transaction).commit();
        assertNotNull(updatedExpense);
        assertEquals(Long.valueOf(1), updatedExpense.getId());
        assertEquals(150.00, updatedExpense.getAmount());
    }

    @Test
    void testDeleteAllExpenses() {
        when(entityManager.createNativeQuery("DELETE e FROM expenses e")).thenReturn(nativeQuery);

        expenseRepository.deleteAll();

        verify(entityManager).createNativeQuery("DELETE e FROM expenses e");
        verify(nativeQuery).executeUpdate();
        verify(transaction).begin();
        verify(transaction).commit();
    }
}
