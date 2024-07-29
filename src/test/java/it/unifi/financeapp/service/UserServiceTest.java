package it.unifi.financeapp.service;

import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @Nested
    @DisplayName("Happy Cases")
    class HappyCases {

        @Test
        void testAddUser() {
            User user = new User("username", "name", "surname", "email");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User result = userService.addUser(user);

            assertNotNull(result);
            Assertions.assertEquals(user.getUsername(), result.getUsername());
            Assertions.assertEquals(user.getName(), result.getName());
            Assertions.assertEquals(user.getSurname(), result.getSurname());
            Assertions.assertEquals(user.getEmail(), result.getEmail());
            verify(userRepository).save(user);
        }
    }

    @Test
    public void testSaveExistingUser() {
        User existingUser = new User("username", "email");
        existingUser.setId(1L); // Simulate an existing category
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.addUser(existingUser);

        assertNotNull(updatedUser);
        Assertions.assertEquals(existingUser.getId(), updatedUser.getId());
        verify(userRepository).save(existingUser);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("username1", "email1");
        User user2 = new User("username2", "email2");
        List<User> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertNotNull(actualUsers);
        Assertions.assertEquals(2, actualUsers.size());
        Assertions.assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }


    @Test
    void testGetAllUsersEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> actualUsers = userService.getAllUsers();

        assertNotNull(actualUsers);
        Assertions.assertTrue(actualUsers.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUser() {
        User originalUser = new User("originalUsername", "name", "surname", "email");
        User updatedUser = new User("updatedUsername", "updatedName", "updatedSurname", "email");

        when(userRepository.update(originalUser)).thenReturn(updatedUser);

        originalUser.setUsername("updatedUsername");
        originalUser.setName("updatedName");
        originalUser.setSurname("updatedSurname");
        User result = userService.updateUser(originalUser);

        assertNotNull(result);
        Assertions.assertEquals(updatedUser.getUsername(), result.getUsername());
        verify(userRepository).update(originalUser);
    }

    @Test
    void testFindUserById() {
        User expectedUser = new User("username", "email");
        Long userId = expectedUser.getId();

        when(userRepository.findById(userId)).thenReturn(expectedUser);

        User result = userService.findUserById(userId);

        assertNotNull(result);
        Assertions.assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

}
