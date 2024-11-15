package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
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
class CategoryPanelIT {
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    CategoryPanel categoryView;
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

    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
        mysqlContainer.stop();
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
        categoryView.setCategoryController(categoryController);
        categoryController.initView();
    }

    @Test
    void testAddCategoryButtonFunctionality() {
        categoryView.setName("New Category");
        categoryView.setDescription("New Description");

        ActionEvent e = new ActionEvent(categoryView.getAddCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : categoryView.getAddCategoryButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        assertTrue(categoryView.getCategoryTable().getModel().getRowCount() > 0, "Table should have one category added.");
        assertEquals("New Category", categoryView.getCategoryTable().getModel().getValueAt(0, 1), "The category name should match.");
    }

    @Test
    void testDeleteCategoryButtonFunctionality() {
        // Prepare the view with one category
        testAddCategoryButtonFunctionality();
        categoryView.getCategoryTable().setRowSelectionInterval(0, 0);

        ActionEvent e = new ActionEvent(categoryView.getDeleteCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : categoryView.getDeleteCategoryButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        assertEquals(0, categoryView.getCategoryTable().getModel().getRowCount(), "Table should be empty after deletion.");
    }

    @AfterEach
    void cleanUpDatabase() {
        categoryService.deleteAll();
    }

}
