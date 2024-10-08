package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ExpenseRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private ExpenseRepositoryImpl expenseRepository;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("TestFinanceAppH2PU");
        em = emf.createEntityManager();
        expenseRepository = new ExpenseRepositoryImpl(em);
        category = new Category("Travel", "Expenses for travel");
        user = new User("john.doe", "john.doe@example.com");
        em.getTransaction().begin();
        em.persist(category);
        em.persist(user);
        em.getTransaction().commit();
    }
    @Test
    void testSaveNewExpense() {
        Expense newExpense = new Expense(category, user, 100.0, "2021-07-16");

        expenseRepository.save(newExpense);

        Expense retrieved = em.find(Expense.class, newExpense.getId());
        assertNotNull(retrieved);
        assertEquals(user, retrieved.getUser());
        assertEquals(category, retrieved.getCategory());
        assertEquals(newExpense.getAmount(), retrieved.getAmount());
        assertEquals(newExpense.getDate(), retrieved.getDate());
    }

    @Test
    void testFindById() {
        Expense newExpense = new Expense(category, user, 100.0, "2021-07-16");

        em.getTransaction().begin();
        em.persist(newExpense);
        em.getTransaction().commit();

        Expense foundExpense = expenseRepository.findById(newExpense.getId());
        assertNotNull(foundExpense);
        assertEquals(user, foundExpense.getUser());
        assertEquals(category, foundExpense.getCategory());
        assertEquals(newExpense.getAmount(), foundExpense.getAmount());
        assertEquals(newExpense.getDate(), foundExpense.getDate());
    }

    @Test
    void testFindAll() {
        Expense expense1 = new Expense(category, user, 100.0, "2021-07-16");
        Expense expense2 = new Expense(category, user, 150.0, "2021-07-17");

        em.getTransaction().begin();
        em.persist(expense1);
        em.persist(expense2);
        em.getTransaction().commit();

        List<Expense> expenses = expenseRepository.findAll();

        assertNotNull(expenses);
        assertEquals(2, expenses.size());
    }

    @Test
    void testUpdateExpense() {
        Expense existingExpense = new Expense(category, user, 100.00, "2022-01-02");
        em.getTransaction().begin();
        em.persist(existingExpense);
        em.getTransaction().commit();

        existingExpense.setDate("2024-01-01");
        expenseRepository.update(existingExpense);

        Expense retrieved = em.find(Expense.class, existingExpense.getId());
        assertEquals("2024-01-01", retrieved.getDate());
    }


    @Test
    void testManageDependencies() {
        Expense expense = spy(new Expense(category, user, 100.00, "2022-01-02"));

        expenseRepository.manageDependencies(expense);

        verify(expense).setUser(user);
        verify(expense).setCategory(category);
    }

    @Test
    void testDeleteExpense() {
        Expense expenseToDelete = new Expense(category, user, 300.00, "2022-01-03");
        em.getTransaction().begin();
        em.persist(expenseToDelete);
        em.getTransaction().commit();

        expenseRepository.delete(expenseToDelete);

        Expense retrieved = em.find(Expense.class, expenseToDelete.getId());
        assertNull(retrieved);
    }

    @Test
    void testSaveExistingExpense() {
        Expense existingExpense = new Expense(category, user, 150.00, "2022-01-02");
        em.getTransaction().begin();
        em.persist(existingExpense);
        em.getTransaction().commit();

        existingExpense.setDate("2024-01-01");
        expenseRepository.save(existingExpense);

        Expense retrieved = em.find(Expense.class, existingExpense.getId());

        assertNotNull(retrieved);
        assertEquals("2024-01-01", retrieved.getDate());
    }

    @Test
    void testDeleteAllExpenses() {
        testSaveNewExpense();
        expenseRepository.deleteAll();

        List<Expense> expenses = expenseRepository.findAll();
        assertTrue(expenses.isEmpty());
    }
}
