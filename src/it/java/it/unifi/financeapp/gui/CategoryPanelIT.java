package it.unifi.financeapp.gui;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.table.TableModel;

import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ExtendWith(GUITestExtension.class)
@Testcontainers
class CategoryPanelIT {
    @SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
    @Container
    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static EntityManagerFactory emf;
    CategoryPanel categoryView;
    CategoryService categoryService;
    CategoryController categoryController;
    private String CATEGORY_NAME = "New Category";
    private String CATEGORY_DESCRIPTION = "New Description";

    @BeforeAll
    static void setUpTestClasses() {
        // Configure JDBC properties dynamically based on Testcontainers
        Map<String, String> overrides = new HashMap<>();
        overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
        overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
        overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
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
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
        categoryService = new CategoryService(categoryRepository);
        categoryService.deleteAll();
        categoryView = new CategoryPanel();
        categoryController = new CategoryController(categoryService, categoryView);
        categoryView.setCategoryController(categoryController);
        categoryController.initView();
    }

    private void addCategory() {
        categoryView.setName(CATEGORY_NAME);
        categoryView.setDescription(CATEGORY_DESCRIPTION);

        ActionEvent e = new ActionEvent(categoryView.getAddCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
        for (ActionListener al : categoryView.getAddCategoryButton().getActionListeners()) {
            al.actionPerformed(e);
        }
    }

    @Test @GUITest
    void testAddCategoryButtonFunctionality() {
        TableModel model = categoryView.getCategoryTable().getModel();
        assertEquals(0, model.getRowCount(), "Table should have zero rows before adding a category");
        addCategory();
        assertEquals(1, model.getRowCount(), "Table should have one row after adding a category");
    }
    
    @Test @GUITest
    void testStatusUpdateAfterAddingCategory() {
        JLabel statusLabel = categoryView.statusLabel;
        assertEquals(" ", statusLabel.getText());
        addCategory();
        execute(() -> categoryView.setStatus("Category added successfully"));
        assertEquals("Category added successfully", statusLabel.getText());
    }
    
    @Test @GUITest
    void testShownCategoryShouldMatchCategoryAdded() {
        addCategory();
        TableModel model = categoryView.getCategoryTable().getModel();
        assertEquals(1, model.getRowCount(), "Table should have one row after adding a category");
        assertEquals(CATEGORY_NAME, model.getValueAt(0, 1), "Category name in the table should match the added category");
        assertEquals(CATEGORY_DESCRIPTION, model.getValueAt(0, 2), "Category Description in the table should match the added category");
    }
    
    @Test @GUITest
    void testDeleteCategoryButtonFunctionality() {
        // Prepare the view with one category
        addCategory();
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