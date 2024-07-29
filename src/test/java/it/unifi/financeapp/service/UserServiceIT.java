package it.unifi.financeapp.service;

import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

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

}
