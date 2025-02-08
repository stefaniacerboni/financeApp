package it.unifi.financeapp.steps;

import it.unifi.financeapp.gui.MainFrame;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.*;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

@Testcontainers
public class TestConfig {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in TeardownClass
    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    public static FrameFixture window;
    public static CategoryService categoryService;
    public static UserService userService;
    public static ExpenseService expenseService;
    public static EntityManager em;

    public static void setUpClass() {
        mysqlContainer.start();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", getJPAOverrides());
        em = emf.createEntityManager();
        setupServices(em);

        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new MainFrame(categoryService, userService, expenseService);
            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return f;
        });
        window = new FrameFixture(frame);
        window.show();
    }

    private static Map<String, String> getJPAOverrides() {
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
        return overrides;
    }

    private static void setupServices(EntityManager em) {
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
        UserRepository userRepository = new UserRepositoryImpl(em);
        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
        categoryService = new CategoryService(categoryRepository);
        userService = new UserService(userRepository);
        expenseService = new ExpenseService(expenseRepository);
    }

    public static void prepareTestData() {
        // Create and persist category
        Category category = new Category("Utilities", "Monthly utility expenses");
        categoryService.addCategory(category);
        // Create and persist user
        User user = new User("john.doe", "John", "Doe", "john.doe@example.com");
        userService.addUser(user);
    }

    public static void tearDownClass() {
        window.cleanUp();
        mysqlContainer.stop();
    }
}