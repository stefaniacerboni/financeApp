package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryPanel;
import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class CategoryControllerIT {
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
        testAddCategoryButtonFunctionality(); // First add a category
        categoryView.getCategoryTable().setRowSelectionInterval(0, 0); // Select the row

        // Simulate delete button click
        ActionEvent e = new ActionEvent(categoryView.getDeleteCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : categoryView.getDeleteCategoryButton().getActionListeners()) {
            al.actionPerformed(e);
        }

        // Assert the row is deleted
        assertEquals(0, categoryView.getCategoryTable().getModel().getRowCount(), "Table should be empty after deletion.");
    }

    @Test
    void testTableSelectionEnablesDeleteButton() {
        testAddCategoryButtonFunctionality();
        assertFalse(categoryView.getDeleteCategoryButton().isEnabled(), "Delete button should initially be disabled.");

        // Simulate table row selection
        categoryView.getCategoryTable().setRowSelectionInterval(0, 0);

        // Assert the delete button is enabled
        assertTrue(categoryView.getDeleteCategoryButton().isEnabled(), "Delete button should be enabled when a row is selected.");
    }
}
