package it.unifi.financeapp.repository;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

	UserRepository userRepository;
	private EntityManagerFactory emf;
	private EntityManager em;

	@BeforeEach
	void init() {
		emf = Persistence.createEntityManagerFactory("TestFinanceAppH2PU");
		em = emf.createEntityManager();
		userRepository = new UserRepositoryImpl(em);
	}

	@AfterEach
	void close() {
		em.close();
		emf.close();
	}

	@Test
	void testFindById() {
		User newUser = new User("Username", "Email");
		em.getTransaction().begin();
		em.persist(newUser);
		em.getTransaction().commit();

		User foundUser = userRepository.findById(newUser.getId());
		assertNotNull(foundUser);
		assertEquals("Username", foundUser.getUsername());
		assertEquals("Email", foundUser.getEmail());
	}

	@Test
	void testFindAll() {
		em.getTransaction().begin();
		User usr1 = new User("UserName1", "Email1");
		User usr2 = new User("UserName2", "Email2");
		em.persist(usr1);
		em.persist(usr2);
		em.getTransaction().commit();

		List<User> users = userRepository.findAll();

		assertNotNull(users);
		assertEquals(2, users.size());
		assertEquals(usr1, users.get(0));
		assertEquals(usr2, users.get(1));
	}

	@Test
	void testSaveNewUser() {
		User newUser = new User("New", "New User Email");

		User res = userRepository.save(newUser);
		assertNotNull(res);
		em.clear();

		User retrieved = em.find(User.class, newUser.getId());
		assertNotNull(retrieved);
		assertEquals("New", retrieved.getUsername());
		assertEquals("New User Email", retrieved.getEmail());

	}

	@Test
	void testSaveExistingUser() {
		User existingUser = new User("Existing", "Existing User Email");
		em.getTransaction().begin();
		em.persist(existingUser);
		em.getTransaction().commit();

		// Detach the entity to simulate a real update scenario
		em.clear();

		existingUser.setEmail("Updated Email");
		User res = userRepository.save(existingUser);
		assertNotNull(res);

		User retrieved = em.find(User.class, existingUser.getId());

		assertNotNull(retrieved);
		assertEquals(existingUser.getName(), retrieved.getName());
		assertEquals("Updated Email", retrieved.getEmail());
	}

	@Test
	void testSaveSameUsernameUser() {
		User existingUser = new User("Existing", "Existing User Email");
		em.getTransaction().begin();
		em.persist(existingUser);
		em.getTransaction().commit();
		User newUser = new User("Existing", "Different User Email");
		assertThrows(PersistenceException.class, () -> userRepository.save(newUser));
	}
	
	@Test
	void testUpdateUser() {
		User user = new User("Existing", "Existing User Description");
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();

		user.setName("Updated Name");
		User res = userRepository.update(user);
		assertNotNull(res);
		em.clear();

		User retrieved = em.find(User.class, user.getId());
		assertEquals("Updated Name", retrieved.getName());
	}
	
	@Test
	void testUpdateSameUsernameUser() {
		User existingUser = new User("Existing", "Existing User Email");
		em.getTransaction().begin();
		em.persist(existingUser);
		em.getTransaction().commit();
		User newUser = new User("Not Existing", "Different User Email");
		em.getTransaction().begin();
		em.persist(newUser);
		em.getTransaction().commit();
		newUser.setUsername("Existing");
		assertThrows(PersistenceException.class, () -> userRepository.update(newUser));
	}

	@Test
	void testDeleteUser() {
		User user = new User("To Be Deleted", "To be deleted Email");
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();

		userRepository.delete(user);
		em.clear();

		User retrieved = em.find(User.class, user.getId());
		assertNull(retrieved);
	}

	@Test
	void testDeleteUserWithDependencies() {
		Category category = new Category("Name", "Description");
		User user = new User("To Be Deleted", "To be deleted Email");
		em.getTransaction().begin();
		em.persist(category);
		em.persist(user);
		em.persist(new Expense(category, user, 0.0, "2024-12-12"));
		em.getTransaction().commit();
		assertThrows(IllegalStateException.class, () -> userRepository.delete(user));
		User retrieved = em.find(User.class, user.getId());
		assertNotNull(retrieved);
	}

	@Test
	void testDeleteAllUsers() {
		User newUser = new User("New", "New User Email");

		User res = userRepository.save(newUser);
		assertNotNull(res);
		em.clear();
		userRepository.deleteAll();
		em.clear();
		// Use a new EntityManager for verification
		EntityManager emVerification = emf.createEntityManager();
		UserRepository userRepositoryVerification = new UserRepositoryImpl(emVerification);

		List<User> users = userRepositoryVerification.findAll();
		assertTrue(users.isEmpty());

		emVerification.close();
	}
}