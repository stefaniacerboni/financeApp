package it.unifi.financeapp.steps;

import it.unifi.financeapp.gui.MainFrame;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.*;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Testcontainers
public class TestConfig {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in TeardownClass
	@Container
	public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29").withDatabaseName("testdb")
			.withUsername("test").withPassword("test");

	public static FrameFixture window;
	public static CategoryService categoryService;
	public static UserService userService;
	public static ExpenseService expenseService;
	public static EntityManager em;
	public static Category category = new Category("Utilities", "Monthly utility expenses");
	public static User user = new User("john.doe", "John", "Doe", "john.doe@example.com");

	public static void setUpClass() {
		mysqlContainer.start();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestFinanceAppPU", getJPAOverrides());
		em = emf.createEntityManager();
		setupServices(em);

		JFrame frame = GuiActionRunner.execute(() -> {
			JFrame f = new MainFrame(categoryService, userService, expenseService);
			f.pack();
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.setVisible(true);
			return f;
		});
		window = new FrameFixture(frame);
		window.show();
	}

	private static Map<String, String> getJPAOverrides() {
		Map<String, String> overrides = new HashMap<>();
		overrides.put("javax.persistence.jdbc.url", mysqlContainer.getJdbcUrl());
		overrides.put("javax.persistence.jdbc.user", mysqlContainer.getUsername());
		overrides.put("javax.persistence.jdbc.password", mysqlContainer.getPassword());
		return overrides;
	}

	private static void setupServices(EntityManager em) {
		CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
		UserRepository userRepository = new UserRepositoryImpl(em);
		ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
		categoryService = new CategoryService(categoryRepository);
		userService = new UserService(userRepository);
		expenseService = new ExpenseService(expenseRepository);
	}

	public static void prepareTestData() {
		// persist category
		categoryService.addCategory(category);
		// persist user
		userService.addUser(user);
	}

	public static void cleanUpDB() {
		expenseService.deleteAll();
		categoryService.deleteAll();
		userService.deleteAll();
		window.robot().waitForIdle();
	}

	public static void tearDownClass() {
		if (window != null)
			window.cleanUp();
		if (mysqlContainer != null)
			mysqlContainer.stop();
	}

	@Given("The database contains the categories with the following values")
	public void theDatabaseContainsTheCategoriesWithTheFollowingValues(DataTable dataTable) {
		// Convert the DataTable into a list of rows; each row is a list of strings.
		List<List<String>> rows = dataTable.asLists(String.class);
		for (List<String> row : rows) {
			String name = row.get(0);
			String description = row.get(1);
			Category category = new Category(name, description);
			// Persist the category (this should set its auto-generated id, etc.)
			categoryService.addCategory(category);
		}
	}

	@And("^The database contains the expenses with the following values$")
	public void theDatabaseContainsTheExpensesWithTheFollowingValues(DataTable dataTable) {
		// Convert the DataTable into a list of rows; each row is a list of strings.
		List<List<String>> rows = dataTable.asLists(String.class);
		for (List<String> row : rows) {
			String amount = row.get(2);
			String date = row.get(3);
			Expense expense = new Expense(category, user, Double.parseDouble(amount), date);
			// Persist the expense (this should set its auto-generated id, etc.)
			expenseService.addExpense(expense);
		}
	}

	@Given("^The database contains the users with the following values$")
	public void theDatabaseContainsTheUsersWithTheFollowingValues(DataTable dataTable) {
		// Convert the DataTable into a list of rows; each row is a list of strings.
		List<List<String>> rows = dataTable.asLists(String.class);
		for (List<String> row : rows) {
			String username = row.get(0);
			String name = row.get(1);
			String surname = row.get(2);
			String email = row.get(3);
			User user = new User(username, name, surname, email);
			// Persist the user (this should set its auto-generated id, etc.)
			userService.addUser(user);
		}
	}

	@Given("^The database contains a category or user connected to an expense$")
	public void theDatabaseContainsACategoryOrUserConnectedToAnExpense() {
		prepareTestData();
		Expense expense = new Expense(category, user, 200.0, "2024-01-01");
		expenseService.addExpense(expense);
	}

}