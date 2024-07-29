package it.unifi.financeapp.service;

import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
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
public class UserServiceIT {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    private static UserService userService;

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

        UserRepository userRepository = new UserRepositoryImpl(em);
        userService = new UserService(userRepository);
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
        userService.deleteAll();
    }

    @Test
    public void testAddAndRetrieveUser() {
        // Setup test data
        User user = new User("username", "name", "surname", "email");
        User savedUser = userService.addUser(user);

        // Retrieve from service
        User retrievedUser = userService.findUserById(savedUser.getId());

        // Assertions
        assertNotNull(retrievedUser);
        Assertions.assertEquals(user.getUsername(), retrievedUser.getUsername());
        Assertions.assertEquals(user.getName(), retrievedUser.getName());
        Assertions.assertEquals(user.getSurname(), retrievedUser.getSurname());
        Assertions.assertEquals(user.getEmail(), retrievedUser.getEmail());
    }


    @Test
    void testAddExistingUser() {
        // Setup test data
        User user = new User("username", "name", "surname", "email");
        user.setId(100L);
        User savedUser = userService.addUser(user);

        // Assertions
        assertNotNull(savedUser);
        Assertions.assertEquals(user.getUsername(), savedUser.getUsername());
        Assertions.assertEquals(user.getName(), savedUser.getName());
        Assertions.assertEquals(user.getSurname(), savedUser.getSurname());
        Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
    }

    @Test
    void testAddThenDeleteUser() {
        User user = new User("username", "email");
        User saved = userService.addUser(user);
        Assertions.assertNotNull(saved, "What sorcery is this? The user vanished upon saving!");

        userService.deleteUser(saved.getId());
        User queriedPostDelete = userService.findUserById(saved.getId());
        assertNull(queriedPostDelete, "The user lingers like a bad odor even after deletion. Intriguing!");
    }

    @Test
    void testAddNullUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));

        assertTrue(exception.getMessage().contains("Cannot add a null user"));
    }

    @Test
    void testUpdateUser() {
        // Create and save an initial user
        User user = new User("username", "email");
        User savedUser = userService.addUser(user);
        assertNotNull(savedUser);

        // Update the user
        user.setUsername("anotherUsername");
        user.setEmail("anotherEmail");
        User updatedUser = userService.updateUser(user);

        // Retrieve and assert changes
        User foundUser = userService.findUserById(updatedUser.getId());
        assertNotNull(foundUser);
        Assertions.assertEquals(updatedUser.getUsername(), foundUser.getUsername());
        Assertions.assertEquals(updatedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        // Create and save an user
        User user = new User("username", "email");
        User savedUser = userService.addUser(user);
        assertNotNull(savedUser);

        // Delete the user
        userService.deleteUser(savedUser.getId());

        // Attempt to retrieve the deleted user
        User foundUser = userService.findUserById(savedUser.getId());
        assertNull(foundUser);
    }

}
