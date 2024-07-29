package it.unifi.financeapp.service;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
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
public class CategoryServiceIT {

    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;

    private static CategoryService categoryService;

    @BeforeAll
    public static void setUp() {
        // Configure JDBC properties dynamically based on Testcontainers
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
        overrides.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");  // MySQL dialect
        overrides.put("hibernate.hbm2ddl.auto", "create-drop");


        // Create EntityManagerFactory with these properties
        emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", overrides);
        EntityManager em = emf.createEntityManager();

        // Instantiate the repository with the EntityManager
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
        categoryService = new CategoryService(categoryRepository);
    }

    @AfterAll
    public static void tearDown() {
        if (emf != null) {
            emf.close();
        }
        mysqlContainer.stop();
    }

    @AfterEach
    void cleanUpDatabase() {
        categoryService.deleteAll();
    }


    @Test
    void testAddAndRetrieveCategory() {
        // Setup test data
        Category category = new Category("Coffee", "Category about coffee");
        Category savedCategory = categoryService.addCategory(category);

        // Retrieve from service
        Category retrievedCategory = categoryService.findCategoryById(savedCategory.getId());

        // Assertions
        assertNotNull(retrievedCategory);
        Assertions.assertEquals(category.getName(), retrievedCategory.getName());
        Assertions.assertEquals(category.getDescription(), retrievedCategory.getDescription());
    }

}
