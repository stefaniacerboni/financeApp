package it.unifi.financeapp.service;


import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.repository.ExpenseRepositoryImpl;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ExpenseServiceIT {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private static ExpenseService expenseService;

    @BeforeAll
    static void setUp() {
        // Configure JDBC properties dynamically based on Testcontainers
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());

        // Create EntityManagerFactory with these properties
        emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", overrides);
        EntityManager em = emf.createEntityManager();

        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
        expenseService = new ExpenseService(expenseRepository);
    }

    @AfterAll
    static void tearDown() {
        if (emf != null)
            emf.close();

        if (mysqlContainer != null)
            mysqlContainer.close();
    }

    @AfterEach
    void cleanUpDatabase() {
        expenseService.deleteAll();
    }


    @Test
    void testAddAndRetrieveExpense() {
        // Setup test data
        Category category = new Category("Coffee", "Category about coffee");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 3.50, "2024-07-15");
        Expense savedExpense = expenseService.addExpense(expense);

        // Retrieve from it.unifi.financeapp.service
        Expense retrievedExpense = expenseService.findExpenseById(savedExpense.getId());

        assertNotNull(retrievedExpense);
        Assertions.assertEquals(category.getName(), retrievedExpense.getCategory().getName());
        Assertions.assertEquals(category.getDescription(), retrievedExpense.getCategory().getDescription());
        Assertions.assertEquals(user.getName(), retrievedExpense.getUser().getName());
        Assertions.assertEquals(user.getEmail(), retrievedExpense.getUser().getEmail());
        Assertions.assertEquals(expense.getAmount(), retrievedExpense.getAmount(), 0.001);
        Assertions.assertEquals(expense.getDate(), retrievedExpense.getDate());
    }

    @Test
    void testAddExistingExpense() {
        // Setup test data
        Category category = new Category("Coffee", "Category about coffee");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 3.50, "2024-07-15");
        expense.setId(100L);
        Expense savedExpense = expenseService.addExpense(expense);

        assertNotNull(savedExpense);
        Assertions.assertEquals(category.getName(), savedExpense.getCategory().getName());
        Assertions.assertEquals(category.getDescription(), savedExpense.getCategory().getDescription());
        Assertions.assertEquals(user.getName(), savedExpense.getUser().getName());
        Assertions.assertEquals(user.getEmail(), savedExpense.getUser().getEmail());
        Assertions.assertEquals(expense.getAmount(), savedExpense.getAmount(), 0.001);
        Assertions.assertEquals(expense.getDate(), savedExpense.getDate());
    }

    @Test
    void testAddNullExpense() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> expenseService.addExpense(null));

        assertTrue(exception.getMessage().contains("Cannot add a null expense"));
    }

    @Test
    void testAddThenDeleteExpense() {
        Category category = new Category("Books", "Category about books");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 62.50, "2024-08-01");
        Expense saved = expenseService.addExpense(expense);
        Assertions.assertNotNull(saved, "What sorcery is this? The expense vanished upon saving!");

        expenseService.deleteExpense(saved.getId());
        Expense queriedPostDelete = expenseService.findExpenseById(saved.getId());
        assertNull(queriedPostDelete, "The expense lingers like a bad odor even after deletion. Intriguing!");
    }

    @Test
    void testUpdateExpense() {
        // Create and save an initial expense
        Category category = new Category("Transportation", "Category about transportation");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 15.75, "2024-07-20");
        Expense savedExpense = expenseService.addExpense(expense);
        assertNotNull(savedExpense);

        // Update the expense
        Category category2 = new Category("Travel", "Category about travel");
        savedExpense.setCategory(category2);
        savedExpense.setAmount(20.00);
        Expense updatedExpense = expenseService.updateExpense(savedExpense);

        // Retrieve and assert changes
        Expense foundExpense = expenseService.findExpenseById(updatedExpense.getId());
        assertNotNull(foundExpense);
        Assertions.assertEquals(category2.getName(), foundExpense.getCategory().getName());
        Assertions.assertEquals(category2.getDescription(), foundExpense.getCategory().getDescription());
        Assertions.assertEquals(20.00, foundExpense.getAmount(), 0.001);
    }

    @Test
    void testFindAll() {
        User user = new User("username1", "email1");
        Category category = new Category("Travel", "Category about travel");
        Expense expense1 = new Expense(category, user, 200.0, "2024-07-22");
        Expense expense2 = new Expense(category, user, 300.0, "2024-07-23");
        Expense savedExpense1 = expenseService.addExpense(expense1);
        Expense savedExpense2 = expenseService.addExpense(expense2);
        assertNotNull(savedExpense1);
        assertNotNull(savedExpense2);
        List<Expense> expectedExpenses = Arrays.asList(expense1, expense2);

        List<Expense> actualExpenses = expenseService.getAllExpenses();
        assertNotNull(actualExpenses);
        Assertions.assertEquals(expectedExpenses.size(), actualExpenses.size());
        assertTrue(expectedExpenses.contains(expense1));
        assertTrue(expectedExpenses.contains(expense2));
    }

    @Test
    void testDeleteExpense() {
        // Create and save an expense
        Category category = new Category("Coffee", "Category about coffee");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 3.50, "2024-07-15");
        Expense savedExpense = expenseService.addExpense(expense);
        assertNotNull(savedExpense);

        // Delete the expense
        expenseService.deleteExpense(savedExpense.getId());

        // Attempt to retrieve the deleted expense
        Expense foundExpense = expenseService.findExpenseById(savedExpense.getId());
        assertNull(foundExpense);
    }

    @Test
    void testDeleteAll() {
        User user = new User("username1", "email1");
        Category category = new Category("Travel", "Category about travel");
        Expense expense = new Expense(category, user, 200.0, "2024-07-22");
        expenseService.addExpense(expense);
        List<Expense> actualExpenses = expenseService.getAllExpenses();
        assertNotNull(actualExpenses);
        Assertions.assertEquals(1, actualExpenses.size());
        expenseService.deleteAll();
        List<Expense> emptyExpenses = expenseService.getAllExpenses();
        Assertions.assertEquals(0, emptyExpenses.size());
    }
}