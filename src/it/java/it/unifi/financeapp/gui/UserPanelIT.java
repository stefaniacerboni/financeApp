package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.UserController;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
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
class UserPanelIT {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private static EntityManagerFactory emf;
    UserPanel userView;
    UserService userService;
    UserController userController;

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
        UserRepository userRepository = new UserRepositoryImpl(em);
        userService = new UserService(userRepository);
        userService.deleteAll();
        userView = new UserPanel();
        userController = new UserController(userService, userView);
        userView.setUserController(userController);
        userController.initView();
    }

    @Test
    void testAddUserButtonFunctionality() {
        // Setting text to inputs
        userView.setUsername("NewUser");
        userView.setName("New User Name");
        userView.setSurname("New User Surname");
        userView.setEmail("New User Email");

        // Simulating button click
        ActionEvent e = new ActionEvent(userView.getAddUserButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : userView.getAddUserButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        // Assert changes
        assertTrue(userView.getUserTable().getModel().getRowCount() > 0, "Table should have one user added.");
        assertEquals("NewUser", userView.getUserTable().getModel().getValueAt(0, 1), "The user username should match.");
        assertEquals("New User Name", userView.getUserTable().getModel().getValueAt(0, 2), "The user name should match.");
        assertEquals("New User Surname", userView.getUserTable().getModel().getValueAt(0, 3), "The user surname should match.");
        assertEquals("New User Email", userView.getUserTable().getModel().getValueAt(0, 4), "The user email should match.");
    }

    @Test
    void testDeleteUserButtonFunctionality() {
        // Prepare the view with one user
        testAddUserButtonFunctionality(); // First add a user
        userView.getUserTable().setRowSelectionInterval(0, 0); // Select the row

        // Simulate delete button click
        ActionEvent e = new ActionEvent(userView.getDeleteUserButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : userView.getDeleteUserButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        // Assert the row is deleted
        assertEquals(0, userView.getUserTable().getModel().getRowCount(), "Table should be empty after deletion.");
    }


    @AfterEach
    void cleanUpDatabase() {
        userService.deleteAll();
    }

}
