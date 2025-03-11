package it.unifi.financeapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class CategoryServiceIT {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
	@Container
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
			.withDatabaseName("testdb").withUsername("test").withPassword("test");

	private static EntityManagerFactory emf;

	private static CategoryService categoryService;

	@BeforeAll
	static void setUp() {
		// Configure JDBC properties dynamically based on Testcontainers
		Map<String, String> overrides = new HashMap<>();
		overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
		overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
		overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());

		// Create EntityManagerFactory with these properties
		emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", overrides);
		EntityManager em = emf.createEntityManager();

		// Instantiate the repository with the EntityManager
		CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
		categoryService = new CategoryService(categoryRepository);
	}

	@AfterAll
	static void tearDown() {
		if (emf != null)
			emf.close();

		if (mysqlContainer != null)
			mysqlContainer.close();
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

		// Retrieve from it.unifi.financeapp.service
		Category retrievedCategory = categoryService.findCategoryById(savedCategory.getId());

		// Assertions
		assertNotNull(retrievedCategory);
		Assertions.assertEquals(category.getName(), retrievedCategory.getName());
		Assertions.assertEquals(category.getDescription(), retrievedCategory.getDescription());
	}

	@Test
	void testAddExistingCategory() {
		// Setup test data
		Category category = new Category("Coffee", "Category about coffee");
		category.setId(100L);
		Category savedCategory = categoryService.addCategory(category);

		// Assertions
		assertNotNull(savedCategory);
		Assertions.assertEquals(category.getName(), savedCategory.getName());
		Assertions.assertEquals(category.getDescription(), savedCategory.getDescription());
	}

	@Test
	void testAddCategoryWithExistingNameShouldThrowException() {
		// Setup test data
		Category category = new Category("Coffee", "Category about coffee");
		Category savedCategory = categoryService.addCategory(category);
		assertNotNull(savedCategory);
		Category sameCategory = new Category("Coffee", "Same Category about coffee");
		assertThrows(ServiceException.class, () -> categoryService.addCategory(sameCategory));
	}

	@Test
	void testUpdateCategoryWithExistingNameShouldThrowException() {
		// Setup test data
		Category category = new Category("Coffee", "Category about coffee");
		Category savedCategory = categoryService.addCategory(category);
		assertNotNull(savedCategory);
		Category newCategory = new Category("No Coffee", "Category not about coffee");
		Category savedNewCategory = categoryService.addCategory(newCategory);
		assertNotNull(savedNewCategory);
		savedNewCategory.setName("Coffee");
		assertThrows(ServiceException.class, () -> categoryService.updateCategory(savedNewCategory));
	}

	@Test
	void testAddThenDeleteCategory() {
		Category category = new Category("Books", "Category about books");
		Category saved = categoryService.addCategory(category);
		assertNotNull(saved, "Category should be saved");

		categoryService.deleteCategory(saved.getId());
		Category queriedPostDelete = categoryService.findCategoryById(saved.getId());
		assertNull(queriedPostDelete, "Category should be deleted");
	}

	@Test
	void testAddNullCategory() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(null));

		assertTrue(exception.getMessage().contains("Cannot add a null category."));
	}

	@Test
	void testUpdateCategory() {
		// Create and save an initial category
		Category category = new Category("Transportation", "Category about transportation");
		Category savedCategory = categoryService.addCategory(category);
		assertNotNull(savedCategory);

		// Update the category
		category.setName("Travel");
		category.setDescription("Category about travel");
		Category updatedCategory = categoryService.updateCategory(category);

		// Retrieve and assert changes
		Category foundCategory = categoryService.findCategoryById(updatedCategory.getId());
		assertNotNull(foundCategory);
		Assertions.assertEquals(updatedCategory.getName(), foundCategory.getName());
		Assertions.assertEquals(updatedCategory.getDescription(), foundCategory.getDescription());
	}

	@Test
	void testDeleteCategory() {
		// Create and save an category
		Category category = new Category("Meals", "Category about meals");
		Category savedCategory = categoryService.addCategory(category);
		assertNotNull(savedCategory);

		// Delete the category
		categoryService.deleteCategory(savedCategory.getId());

		// Attempt to retrieve the deleted category
		Category foundCategory = categoryService.findCategoryById(savedCategory.getId());
		assertNull(foundCategory);
	}

	@Test
	void testFindAll() {
		Category category1 = new Category("Meals", "Category about meals");
		Category category2 = new Category("Transportation", "Category about transportation");
		Category savedCategory1 = categoryService.addCategory(category1);
		Category savedCategory2 = categoryService.addCategory(category2);
		assertNotNull(savedCategory1);
		assertNotNull(savedCategory2);
		List<Category> expectedCategory = Arrays.asList(category1, category2);

		List<Category> actualCategories = categoryService.getAllCategories();
		assertNotNull(actualCategories);
		Assertions.assertEquals(expectedCategory.size(), actualCategories.size());
		Assertions.assertEquals(expectedCategory, actualCategories);
		assertTrue(expectedCategory.contains(savedCategory1));
		assertTrue(expectedCategory.contains(savedCategory2));
	}

	@Test
	void testDeleteAll() {
		Category category1 = new Category("Meals", "Category about meals");
		categoryService.addCategory(category1);
		List<Category> actualCategories = categoryService.getAllCategories();
		assertNotNull(actualCategories);
		Assertions.assertEquals(1, actualCategories.size());
		categoryService.deleteAll();
		List<Category> emptyCategories = categoryService.getAllCategories();
		Assertions.assertEquals(0, emptyCategories.size());
	}

	@Test
	void testNewCategoryConcurrent() {
		Category category = new Category("Name", "Description");
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				categoryService.addCategory(category);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		})).peek(Thread::start).collect(Collectors.toList());
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
		assertThat(categoryService.getAllCategories()).containsExactly(category);
	}

}