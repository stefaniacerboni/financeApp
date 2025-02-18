package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryPanel;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class CategoryControllerIT {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
	@Container
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
			.withDatabaseName("testdb").withUsername("test").withPassword("test");

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
		categoryView.setName("New Category");
		categoryView.setDescription("New Description");

		ActionEvent e = new ActionEvent(categoryView.getAddCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : categoryView.getAddCategoryButton().getActionListeners()) {
			al.actionPerformed(e);
		}
	}

	@Test
	void testAddCategoryButtonFunctionality() {
		addCategory();
		List<Category> categoryList = categoryService.getAllCategories();
		assertEquals(1, categoryList.size());
		Category found = categoryList.get(0);
		assertEquals("New Category", found.getName());
		assertEquals("New Description", found.getDescription());
	}

	@Test
	void testDeleteCategoryButtonFunctionality() {
		// Prepare the view with one category
		addCategory(); // First add a category
		categoryView.getCategoryTable().setRowSelectionInterval(0, 0); // Select the row

		// Simulate delete button click
		ActionEvent e = new ActionEvent(categoryView.getDeleteCategoryButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : categoryView.getDeleteCategoryButton().getActionListeners()) {
			al.actionPerformed(e);
		}

		// Assert the row is deleted
		List<Category> categoryList = categoryService.getAllCategories();
		assertEquals(0, categoryList.size());
	}

	@AfterEach
	void cleanUpDatabase() {
		categoryService.deleteAll();
	}

}