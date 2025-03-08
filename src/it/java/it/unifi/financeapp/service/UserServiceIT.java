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

import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Testcontainers
class UserServiceIT {
	@SuppressWarnings("resource") // We explicitly close mysqlContainer in @AfterAll
	@Container
	public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.29")
			.withDatabaseName("testdb").withUsername("test").withPassword("test");

	private static EntityManagerFactory emf;
	private static UserService userService;

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

		UserRepository userRepository = new UserRepositoryImpl(em);
		userService = new UserService(userRepository);
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
		userService.deleteAll();
	}

	@Test
	void testAddAndRetrieveUser() {
		// Setup test data
		User user = new User("username", "name", "surname", "email");
		User savedUser = userService.addUser(user);

		// Retrieve from it.unifi.financeapp.service
		User retrievedUser = userService.findUserById(savedUser.getId());

		assertNotNull(retrievedUser);
		Assertions.assertEquals(user.getUsername(), retrievedUser.getUsername());
		Assertions.assertEquals(user.getName(), retrievedUser.getName());
		Assertions.assertEquals(user.getSurname(), retrievedUser.getSurname());
		Assertions.assertEquals(user.getEmail(), retrievedUser.getEmail());
	}

	@Test
	void testAddExistingUser() {
		// Setup test data
		User user = new User("username", "name", "surname", "email");
		user.setId(100L);
		User savedUser = userService.addUser(user);

		assertNotNull(savedUser);
		Assertions.assertEquals(user.getUsername(), savedUser.getUsername());
		Assertions.assertEquals(user.getName(), savedUser.getName());
		Assertions.assertEquals(user.getSurname(), savedUser.getSurname());
		Assertions.assertEquals(user.getEmail(), savedUser.getEmail());
	}

	@Test
	void testAddUserWithExistingUsernameShouldThrowException() {
		// Setup test data
		User user = new User("username", "name", "surname", "email");
		User savedUser = userService.addUser(user);
		assertNotNull(savedUser);
		User sameUser = new User("username", "differentEmail");
		assertThrows(ServiceException.class, () -> userService.addUser(sameUser));
	}
	
	@Test
	void testNewUserConcurrent() {
		User user = new User("username", "name", "surname", "email");
		List<Thread> threads = IntStream.range(0,  10).mapToObj( i -> new Thread(() -> {
			try {
				userService.addUser(user);
			}catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		))
				.peek(t -> t.start())
				.collect(Collectors.toList());
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t-> t.isAlive()));
		assertThat(userService.getAllUsers()).containsExactly(user);
	}

	
	@Test
	void testUpdateUserWithExistingUsernameShouldThrowException() {
		// Setup test data
		User user = new User("username", "name", "surname", "email");
		User savedUser = userService.addUser(user);
		assertNotNull(savedUser);
		User newUser = new User("different username", "differentEmail");
		User savedNewUser = userService.addUser(newUser);
		assertNotNull(savedNewUser);
		savedNewUser.setUsername("username");
		assertThrows(ServiceException.class, () -> userService.updateUser(savedNewUser));
	}

	@Test
	void testAddThenDeleteUser() {
		User user = new User("username", "email");
		User saved = userService.addUser(user);
		Assertions.assertNotNull(saved, "What sorcery is this? The user vanished upon saving!");

		userService.deleteUser(saved.getId());
		User queriedPostDelete = userService.findUserById(saved.getId());
		assertNull(queriedPostDelete, "The user lingers like a bad odor even after deletion. Intriguing!");
	}

	@Test
	void testAddNullUser() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));

		assertTrue(exception.getMessage().contains("Cannot add a null user"));
	}

	@Test
	void testUpdateUser() {
		// Create and save an initial user
		User user = new User("username", "email");
		User savedUser = userService.addUser(user);
		assertNotNull(savedUser);

		// Update the user
		user.setUsername("anotherUsername");
		user.setEmail("anotherEmail");
		User updatedUser = userService.updateUser(user);

		// Retrieve and assert changes
		User foundUser = userService.findUserById(updatedUser.getId());
		assertNotNull(foundUser);
		Assertions.assertEquals(updatedUser.getUsername(), foundUser.getUsername());
		Assertions.assertEquals(updatedUser.getEmail(), foundUser.getEmail());
	}

	@Test
	void testDeleteUser() {
		// Create and save an user
		User user = new User("username", "email");
		User savedUser = userService.addUser(user);
		assertNotNull(savedUser);

		// Delete the user
		userService.deleteUser(savedUser.getId());

		// Attempt to retrieve the deleted user
		User foundUser = userService.findUserById(savedUser.getId());
		assertNull(foundUser);
	}

	@Test
	void testFindAll() {
		User user1 = new User("username1", "email1");
		User user2 = new User("username2", "email2");
		User savedUser1 = userService.addUser(user1);
		User savedUser2 = userService.addUser(user2);
		assertNotNull(savedUser1);
		assertNotNull(savedUser2);
		List<User> expectedUsers = Arrays.asList(user1, user2);

		List<User> actualCategories = userService.getAllUsers();
		assertNotNull(actualCategories);
		Assertions.assertEquals(expectedUsers.size(), actualCategories.size());
		Assertions.assertEquals(expectedUsers, actualCategories);
		assertTrue(expectedUsers.contains(savedUser1));
		assertTrue(expectedUsers.contains(savedUser2));
	}

	@Test
	void testDeleteAll() {
		User user1 = new User("username1", "email1");
		userService.addUser(user1);
		List<User> actualUsers = userService.getAllUsers();
		assertNotNull(actualUsers);
		Assertions.assertEquals(1, actualUsers.size());
		userService.deleteAll();
		List<User> emptyUsers = userService.getAllUsers();
		Assertions.assertEquals(0, emptyUsers.size());
	}
	
	
}