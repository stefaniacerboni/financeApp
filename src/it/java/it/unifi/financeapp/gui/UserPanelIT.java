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

import it.unifi.financeapp.controller.UserController;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
import it.unifi.financeapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ExtendWith(GUITestExtension.class)
@Testcontainers
class UserPanelIT {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
	@Container
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
			.withDatabaseName("testdb").withUsername("test").withPassword("test");
	private static EntityManagerFactory emf;
	UserPanel userView;
	UserService userService;
	UserController userController;
	private String USER_USERNAME = "NewUser";
	private String USER_NAME = "New User Name";
	private String USER_SURNAME = "New User Surname";
	private String USER_EMAIL = "New User Email";

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
		UserRepository userRepository = new UserRepositoryImpl(em);
		userService = new UserService(userRepository);
		userService.deleteAll();
		userView = new UserPanel();
		userController = new UserController(userService, userView);
		userView.setUserController(userController);
		userController.initView();
	}

	private void addUser() {
		// Setting text to inputs
		userView.setUsername(USER_USERNAME);
		userView.setName(USER_NAME);
		userView.setSurname(USER_SURNAME);
		userView.setEmail(USER_EMAIL);

		// Simulating button click
		ActionEvent e = new ActionEvent(userView.getAddUserButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : userView.getAddUserButton().getActionListeners()) {
			al.actionPerformed(e);
		}
	}

	@Test
	@GUITest
	void testAddUserButtonFunctionality() {
		TableModel model = userView.getUserTable().getModel();
		assertEquals(0, model.getRowCount(), "Table should have zero rows before adding a user");
		addUser();
		// Assert changes
		assertEquals(1, model.getRowCount(), "Table should have one row after adding a user");
	}

	@Test
	@GUITest
	void testStatusUpdateAfterAddingUser() {
		JLabel statusLabel = userView.statusLabel;
		assertEquals(" ", statusLabel.getText());
		addUser();
		execute(() -> userView.setStatus("User added successfully"));
		assertEquals("User added successfully", statusLabel.getText());
	}

	@Test
	@GUITest
	void testShownUserShouldMatchUserAdded() {
		addUser();
		TableModel model = userView.getUserTable().getModel();
		assertEquals(1, model.getRowCount(), "Table should have one row after adding a user");
		assertEquals(USER_USERNAME, model.getValueAt(0, 1), "User Username in the table should match the added user");
		assertEquals(USER_NAME, model.getValueAt(0, 2), "User Name in the table should match the added user");
		assertEquals(USER_SURNAME, model.getValueAt(0, 3), "User Surname in the table should match the added user");
		assertEquals(USER_EMAIL, model.getValueAt(0, 4), "User Email in the table should match the added user");

	}

	@Test
	@GUITest
	void testDeleteUserButtonFunctionality() {
		// Prepare the view with one user
		addUser(); // First add a user
		userView.getUserTable().setRowSelectionInterval(0, 0); // Select the row

		// Simulate delete button click
		ActionEvent e = new ActionEvent(userView.getDeleteUserButton(), ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener al : userView.getDeleteUserButton().getActionListeners()) {
			al.actionPerformed(e);
		}

		// Assert the row is deleted
		assertEquals(0, userView.getUserTable().getModel().getRowCount(), "Table should be empty after deletion.");
	}

	@AfterEach
	void cleanUpDatabase() {
		userService.deleteAll();
	}

}