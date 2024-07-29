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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class ExpenseServiceIT {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private static ExpenseService expenseService;

    @BeforeAll
    public static void setUp() {
        // Configure JDBC properties dynamically based on Testcontainers
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
        overrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");  // MySQL dialect

        // Create EntityManagerFactory with these properties
        emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", overrides);
        EntityManager em = emf.createEntityManager();

        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
        expenseService = new ExpenseService(expenseRepository);
    }

    @AfterAll
    public static void tearDown() {
        if (emf != null) {
            emf.close();
        }
        mysqlContainer.stop();
    }

    @AfterEach
    public void cleanUpDatabase() {
        expenseService.deleteAll();
    }


    @Test
    public void testAddAndRetrieveExpense() {
        // Setup test data
        Category category = new Category("Coffee", "Category about coffee");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 3.50, "2024-07-15");
        Expense savedExpense = expenseService.addExpense(expense);

        // Retrieve from service
        Expense retrievedExpense = expenseService.findExpenseById(savedExpense.getId());

        // Assertions
        assertNotNull(retrievedExpense);
        Assertions.assertEquals(category.getName(), retrievedExpense.getCategory().getName());
        Assertions.assertEquals(category.getDescription(), retrievedExpense.getCategory().getDescription());
        Assertions.assertEquals(user.getName(), retrievedExpense.getUser().getName());
        Assertions.assertEquals(user.getEmail(), retrievedExpense.getUser().getEmail());
        Assertions.assertEquals(expense.getAmount(), retrievedExpense.getAmount(), 0.001);
        Assertions.assertEquals(expense.getDate(), retrievedExpense.getDate());
    }

    @Test
    public void testAddExistingExpense() {
        // Setup test data
        Category category = new Category("Coffee", "Category about coffee");
        User user = new User("username", "email");
        Expense expense = new Expense(category, user, 3.50, "2024-07-15");
        expense.setId(100L);
        Expense savedExpense = expenseService.addExpense(expense);

        // Assertions
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


}
