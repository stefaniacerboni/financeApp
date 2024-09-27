package it.unifi.financeapp.repository;


import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    UserRepository userRepository;
    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        emf = Persistence.createEntityManagerFactory("TestFinanceAppH2PU");
        em = emf.createEntityManager();
        userRepository = new UserRepositoryImpl(em);
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
    }

    @Test
    void testSaveNewUser() {
        User newUser = new User("New", "New User Email");

        userRepository.save(newUser);

        User retrieved = em.find(User.class, newUser.getId());
        assertNotNull(retrieved);
        assertEquals("New", retrieved.getUsername());
    }

    @Test
    void testSaveExistingUser() {
        User existingUser = new User("Existing", "Existing User Email");
        em.getTransaction().begin();
        em.persist(existingUser);
        em.getTransaction().commit();

        existingUser.setEmail("Updated Email");
        userRepository.save(existingUser);

        User retrieved = em.find(User.class, existingUser.getId());

        assertNotNull(retrieved);
        assertEquals("Updated Email", retrieved.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = new User("Existing", "Existing User Description");
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        user.setName("Updated Name");
        userRepository.update(user);

        User retrieved = em.find(User.class, user.getId());
        assertEquals("Updated Name", retrieved.getName());
    }

    @Test
    void testDeleteUser() {
        User user = new User("To Be Deleted", "To be deleted Email");
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        userRepository.delete(user);

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
        testSaveNewUser();
        userRepository.deleteAll();

        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty());
    }
}
