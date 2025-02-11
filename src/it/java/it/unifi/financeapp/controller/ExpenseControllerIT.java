package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.ExpensePanel;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.*;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class ExpenseControllerIT {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private static EntityManagerFactory emf;
    ExpensePanel expenseView;
    ExpenseService expenseService;
    UserService userService;
    CategoryService categoryService;
    ExpenseController expenseController;

    @BeforeAll
    static void setUpTestClasses() {
        // Configure JDBC properties dynamically based on Testcontainers
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
        // Create EntityManagerFactory with these properties
        emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", overrides);
    }

    @AfterAll
    static void tearDown() {
        if (emf != null)
            emf.close();

        if (mysqlContainer != null)
            mysqlContainer.close();
    }

    @BeforeEach
    void setup() {

        EntityManager em = emf.createEntityManager();

        // Instantiate the repository with the EntityManager
        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
        expenseService = new ExpenseService(expenseRepository);
        expenseService.deleteAll();

        CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
        categoryService = new CategoryService(categoryRepository);
        categoryService.deleteAll();
        categoryService.addCategory(new Category("Category Name", "Category Description"));

        UserRepository userRepository = new UserRepositoryImpl(em);
        userService = new UserService(userRepository);
        userService.deleteAll();
        userService.addUser(new User("Test Username", "Test User Email"));

        expenseView = new ExpensePanel();
        expenseController = new ExpenseController(expenseService, categoryService, userService, expenseView);
        expenseView.setExpenseController(expenseController);
        expenseController.initView();
        expenseController.updateData();
    }

    private void addExpense() {
        // Setting text to inputs
        expenseView.getUserComboBox().setSelectedIndex(0);
        expenseView.getCategoryComboBox().setSelectedIndex(0);
        expenseView.setAmount("50");
        expenseView.setDate("2024-09-05");

        // Simulating button click
        ActionEvent e = new ActionEvent(expenseView.getAddExpenseButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : expenseView.getAddExpenseButton().getActionListeners()) {
            al.actionPerformed(e);
        }
    }

    @Test
    void testAddExpenseButtonFunctionality() {
        addExpense();
        List<Expense> expenseList = expenseService.getAllExpenses();
        assertEquals(1, expenseList.size());
        Expense found = expenseList.get(0);
        // Assert changes
        assertEquals("Test Username", found.getUser().getUsername(), "The expense username should match.");
        assertEquals("Category Name", found.getCategory().getName(), "The expense's category name should match.");
        assertEquals(50.0, found.getAmount(), "The amount should match.");
        assertEquals("2024-09-05", found.getDate(), "The date should match.");
    }

    @Test
    void testDeleteExpenseButtonFunctionality() {
        // Prepare the view with one expense
        addExpense(); // First add a expense
        expenseView.getExpenseTable().setRowSelectionInterval(0, 0); // Select the row

        // Simulate delete button click
        ActionEvent e = new ActionEvent(expenseView.getDeleteExpenseButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : expenseView.getDeleteExpenseButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        // Assert the expense is deleted
        assertEquals(0, expenseService.getAllExpenses().size());
    }

    @AfterEach
    void cleanUpDatabase() {
        expenseService.deleteAll();
        categoryService.deleteAll();
        userService.deleteAll();
    }
}