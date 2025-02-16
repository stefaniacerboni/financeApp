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

import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.repository.ExpenseRepositoryImpl;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ExtendWith(GUITestExtension.class)
@Testcontainers
class ExpensePanelIT {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
	@Container
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
			.withDatabaseName("testdb").withUsername("test").withPassword("test");
	private static EntityManagerFactory emf;
	ExpensePanel expenseView;
	ExpenseService expenseService;
	UserService userService;
	CategoryService categoryService;
	ExpenseController expenseController;

	private Category EXPENSE_CATEGORY = new Category("Category Name", "Category Description");
	private User EXPENSE_USER = new User("Test Username", "Test User Email");
	private double EXPENSE_AMOUNT = 50.0;
	private String EXPENSE_DATE = "2024-09-05";

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
		ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
		expenseService = new ExpenseService(expenseRepository);
		expenseService.deleteAll();

		CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
		categoryService = new CategoryService(categoryRepository);
		categoryService.deleteAll();
		categoryService.addCategory(EXPENSE_CATEGORY);

		UserRepository userRepository = new UserRepositoryImpl(em);
		userService = new UserService(userRepository);
		userService.deleteAll();
		userService.addUser(EXPENSE_USER);

		expenseView = new ExpensePanel();
		expenseController = new ExpenseController(expenseService, categoryService, userService, expenseView);
		expenseView.setExpenseController(expenseController);
		expenseController.initView();
		expenseController.updateData();
	}

	private void addExpense() {
		// Setting text to inputs
		expenseView.getUserComboBox().setSelectedIndex(0);
		expenseView.getCategoryComboBox().setSelectedIndex(0);
		expenseView.setAmount(String.valueOf(EXPENSE_AMOUNT));
		expenseView.setDate(EXPENSE_DATE);

		// Simulating button click
		ActionEvent e = new ActionEvent(expenseView.getAddExpenseButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : expenseView.getAddExpenseButton().getActionListeners()) {
			al.actionPerformed(e);
		}
	}

	@Test
	@GUITest
	void testAddExpenseButtonFunctionality() {
		TableModel model = expenseView.getExpenseTable().getModel();
		assertEquals(0, model.getRowCount(), "Table should have zero rows before adding an expense");
		addExpense();
		assertEquals(1, model.getRowCount(), "Table should have one row after adding an expense");
	}

	@Test
	@GUITest
	void testStatusUpdateAfterAddingExpense() {
		JLabel statusLabel = expenseView.statusLabel;
		assertEquals(" ", statusLabel.getText());
		addExpense();
		execute(() -> expenseView.setStatus("Expense added successfully"));
		assertEquals("Expense added successfully", statusLabel.getText());
	}

	@Test
	@GUITest
	void testShownExpenseShouldMatchExpenseAdded() {
		addExpense();
		TableModel model = expenseView.getExpenseTable().getModel();
		assertEquals(1, model.getRowCount(), "Table should have one row after adding an expense");
		assertEquals(EXPENSE_USER.getUsername(), model.getValueAt(0, 1),
				"Expense user's username in the table should match the added expense");
		assertEquals(EXPENSE_CATEGORY.getName(), model.getValueAt(0, 2),
				"Expense category's name in the table should match the added expense");
		assertEquals(EXPENSE_AMOUNT, model.getValueAt(0, 3),
				"Expense amount in the table should match the added expense");
		assertEquals(EXPENSE_DATE, model.getValueAt(0, 4), "Expense date in the table should match the added expense");
	}

	@Test
	@GUITest
	void testDeleteExpenseButtonFunctionality() {
		// Prepare the view with one expense
		addExpense();
		expenseView.getExpenseTable().setRowSelectionInterval(0, 0);

		// Simulate delete button click
		ActionEvent e = new ActionEvent(expenseView.getDeleteExpenseButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : expenseView.getDeleteExpenseButton().getActionListeners()) {
			al.actionPerformed(e);
		}

		// Assert the row is deleted
		assertEquals(0, expenseView.getExpenseTable().getModel().getRowCount(),
				"Table should be empty after deletion.");
	}

	@AfterEach
	void cleanUpDatabase() {
		expenseService.deleteAll();
		categoryService.deleteAll();
		userService.deleteAll();
	}

}
