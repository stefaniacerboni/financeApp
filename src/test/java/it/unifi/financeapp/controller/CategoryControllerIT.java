package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryPanel;
import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class CategoryControllerIT {
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private static EntityManagerFactory emf;
    CategoryView categoryView;
    CategoryService categoryService;
    CategoryController categoryController;

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

    @BeforeEach
    void setup() {

        EntityManager em = emf.createEntityManager();

        // Instantiate the repository with the EntityManager
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
        categoryService = new CategoryService(categoryRepository);
        categoryService.deleteAll();
        categoryView = new CategoryPanel();
        categoryController = new CategoryController(categoryService, categoryView);
        categoryController.initView();
    }
}
