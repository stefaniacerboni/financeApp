package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.*;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class ExpensePanelIT {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
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
        overrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");  // MySQL dialect
        overrides.put("hibernate.hbm2ddl.auto", "create-drop");
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

    @Test
    void testAddExpenseButtonFunctionality() {
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

        // Assert changes
        assertTrue(expenseView.getExpenseTable().getModel().getRowCount() > 0, "Table should have one expense added.");
        assertEquals("Test Username", expenseView.getExpenseTable().getModel().getValueAt(0, 1), "The expense username should match.");
        assertEquals("Category Name", expenseView.getExpenseTable().getModel().getValueAt(0, 2), "The username should match.");
        assertEquals(50.0, expenseView.getExpenseTable().getModel().getValueAt(0, 3), "The amount should match.");
        assertEquals("2024-09-05", expenseView.getExpenseTable().getModel().getValueAt(0, 4), "The date should match.");
    }

    @Test
    void testDeleteExpenseButtonFunctionality() {
        // Prepare the view with one expense
        testAddExpenseButtonFunctionality();
        expenseView.getExpenseTable().setRowSelectionInterval(0, 0);

        // Simulate delete button click
        ActionEvent e = new ActionEvent(expenseView.getDeleteExpenseButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : expenseView.getDeleteExpenseButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        // Assert the row is deleted
        assertEquals(0, expenseView.getExpenseTable().getModel().getRowCount(), "Table should be empty after deletion.");
    }

    @AfterEach
    void cleanUpDatabase() {
        expenseService.deleteAll();
        categoryService.deleteAll();
        userService.deleteAll();
    }

}

