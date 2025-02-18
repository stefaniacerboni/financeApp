package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
	void init() {
		emf = Persistence.createEntityManagerFactory("TestFinanceAppH2PU");
		em = emf.createEntityManager();
		expenseRepository = new ExpenseRepositoryImpl(em);
		// setup
		category = new Category("Travel", "Expenses for travel");
		user = new User("john.doe", "john.doe@example.com");
		em.getTransaction().begin();
		em.persist(category);
		em.persist(user);
		em.getTransaction().commit();
	}

	@AfterEach
	void close() {
		em.close();
		emf.close();
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
		assertEquals(expense1, expenses.get(0));
		assertEquals(expense2, expenses.get(1));
	}

	@Test
	void testSaveNewExpense() {
		Expense newExpense = new Expense(category, user, 100.0, "2021-07-16");

		Expense res = expenseRepository.save(newExpense);
		assertNotNull(res);

		Expense retrieved = em.find(Expense.class, newExpense.getId());
		assertNotNull(retrieved);
		assertEquals(user, retrieved.getUser());
		assertEquals(category, retrieved.getCategory());
		assertEquals(newExpense.getAmount(), retrieved.getAmount());
		assertEquals(newExpense.getDate(), retrieved.getDate());
	}

	@Test
	void testSaveExistingExpense() {
		Expense existingExpense = new Expense(category, user, 150.00, "2022-01-02");
		em.getTransaction().begin();
		em.persist(existingExpense);
		em.getTransaction().commit();

		// Detach the entity to simulate a real update scenario
		em.clear();

		existingExpense.setCategory(new Category("School", "Expenses for school"));
		existingExpense.setDate("2024-01-01");
		Expense res = expenseRepository.save(existingExpense);
		assertNotNull(res);

		Expense retrieved = em.find(Expense.class, existingExpense.getId());

		assertNotNull(retrieved);
		assertEquals(existingExpense.getUser(), retrieved.getUser());
		assertEquals(existingExpense.getCategory(), retrieved.getCategory());
		assertEquals(existingExpense.getAmount(), retrieved.getAmount());
		assertEquals("2024-01-01", retrieved.getDate());
	}

	@Test
	void testUpdateExpense() {
		Expense existingExpense = new Expense(category, user, 100.00, "2022-01-02");
		em.getTransaction().begin();
		em.persist(existingExpense);
		em.getTransaction().commit();

		existingExpense.setCategory(new Category("School", "Expenses for school"));
		existingExpense.setDate("2024-01-01");
		Expense res = expenseRepository.update(existingExpense);
		assertNotNull(res);
		em.clear();

		Expense retrieved = em.find(Expense.class, existingExpense.getId());
		assertEquals(existingExpense.getUser(), retrieved.getUser());
		assertEquals(existingExpense.getCategory(), retrieved.getCategory());
		assertEquals(existingExpense.getAmount(), retrieved.getAmount());
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
		em.clear();

		Expense retrieved = em.find(Expense.class, expenseToDelete.getId());
		assertNull(retrieved);
	}

	@Test
	void testDeleteAllExpenses() {
		Expense newExpense = new Expense(category, user, 100.0, "2021-07-16");

		Expense res = expenseRepository.save(newExpense);
		assertNotNull(res);
		expenseRepository.deleteAll();
		em.clear();
		// Use a new EntityManager for verification
		EntityManager emVerification = emf.createEntityManager();
		ExpenseRepository expenseRepositoryVerification = new ExpenseRepositoryImpl(emVerification);

		List<Expense> expenses = expenseRepositoryVerification.findAll();
		assertTrue(expenses.isEmpty());

		emVerification.close();
	}
}