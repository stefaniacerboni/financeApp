package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.UserPanel;
import it.unifi.financeapp.model.User;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class UserControllerIT {
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
        List<User> userList = userService.getAllUsers();
        assertEquals(1, userList.size());
        User found = userList.get(0);
        assertEquals("NewUser", found.getUsername(), "The user username should match.");
        assertEquals("New User Name", found.getName(), "The user name should match.");
        assertEquals("New User Surname", found.getSurname(), "The user surname should match.");
        assertEquals("New User Email", found.getEmail(), "The user email should match.");
    }

    @AfterEach
    void cleanUpDatabase() {
        userService.deleteAll();
    }

}
