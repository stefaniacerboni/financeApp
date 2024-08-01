package it.unifi.financeapp.repository;


import it.unifi.financeapp.model.User;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    @Mock
    EntityManager entityManager;
    UserRepositoryImpl userRepository;
    @Mock
    private TypedQuery<User> typedQuery;
    @Mock
    private NativeQuery<User> nativeQuery;
    @Mock
    private EntityTransaction transaction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(entityManager.getTransaction()).thenReturn(transaction);
        userRepository = new UserRepositoryImpl(entityManager);
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        User mockUser = new User("Username", "Email");
        when(entityManager.find(User.class, id)).thenReturn(mockUser);

        User result = userRepository.findById(id);
        verify(entityManager).find(User.class, id);
        assertEquals("Username", result.getUsername());
        assertEquals("Email", result.getEmail());
    }

    @Test
    public void testFindAll() {
        User user1 = new User("Username", "Email");
        User user2 = new User("Username1", "Email1");
        when(entityManager.createQuery("SELECT u FROM User u", User.class)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userRepository.findAll();

        assertNotNull(users);
        assertEquals(2, users.size());
        verify(entityManager).createQuery("SELECT u FROM User u", User.class);
    }

    @Test
    public void testSaveNewUser() {
        User newUser = new User("New", "New User Email");

        userRepository.save(newUser);

        verify(entityManager).persist(newUser);
        verify(transaction).begin();
        verify(transaction).commit();
    }

    @Test
    public void testSaveExistingUser() {
        User existingUser = new User("Existing", "Existing User Email");
        existingUser.setId(1L);  // Simulate an existing user

        when(entityManager.merge(existingUser)).thenReturn(existingUser);

        User updatedUser = userRepository.save(existingUser);

        verify(entityManager).merge(existingUser);
        verify(entityManager, never()).persist(existingUser);
        assertNotNull(updatedUser);
        assertEquals(Long.valueOf(1), updatedUser.getId());
    }

    @Test
    public void testUpdateUser() {
        User user = new User("Existing", "Existing User Email");
        user.setId(1L);

        when(entityManager.merge(user)).thenReturn(user);

        User updated = userRepository.update(user);

        verify(entityManager).merge(user);
        assertEquals("Existing", updated.getUsername());
    }

    @Test
    public void testDeleteUser() {
        User user = new User("To Be Deleted", "To be deleted Email");
        user.setId(1L);

        userRepository.delete(user);

        verify(entityManager).remove(user);
    }

    @Test
    public void testDeleteAllUsers() {
        when(entityManager.createNativeQuery("DELETE u FROM users u")).thenReturn(nativeQuery);

        userRepository.deleteAll();

        verify(entityManager).createNativeQuery("DELETE u FROM users u");
    }
}
