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

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

}
