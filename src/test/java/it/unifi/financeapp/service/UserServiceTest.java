package it.unifi.financeapp.service;

import it.unifi.financeapp.model.User;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.service.exceptions.InvalidUserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        @Test
        void testDeleteUser() {
            User user = new User("username", "name", "surname", "email");
            Long userId = user.getId();

            when(userRepository.findById(userId)).thenReturn(user);

            userService.deleteUser(userId);

            // Verify findById was called to fetch the user
            verify(userRepository).findById(userId);

            // Verify delete was called with the fetched user
            verify(userRepository).delete(user);
        }

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

        @Test
        void testDeleteAll() {
            userService.deleteAll();
            verify(userRepository).deleteAll();
        }
    }

    @Nested
    @DisplayName("Error Cases")
    class ErrorCases {
        @Test
        void testAddNullUserThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> userService.addUser(null));
        }

        @Test
        void testAddUserThrowsPersistenceException() {
            User user = new User("username", "name", "surname", "email");
            doThrow(new PersistenceException("Could not persist user"))
                    .when(userRepository).save(any(User.class));
            assertThrows(PersistenceException.class, () -> userService.addUser(user));
        }


        @Test
        void testAddUserDatabaseError() {
            User user = new User("username", "name", "surname", "email");
            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));
            Exception exception = assertThrows(RuntimeException.class, () -> userService.addUser(user));

            Assertions.assertTrue(exception.getMessage().contains("Database error"));
        }

        @Test
        void testAddUserWithInvalidData() {
            User invalidUser = new User("", "  ");

            assertThrows(InvalidUserException.class, () -> userService.addUser(invalidUser));
        }


        @Test
        void testAddUserWithEmptyDate() {
            User userWithEmptyField = new User(null, "email");

            Exception exception = assertThrows(InvalidUserException.class, () -> userService.addUser(userWithEmptyField));

            Assertions.assertEquals("Username must be not null.", exception.getMessage());

            // Also test for empty email
            userWithEmptyField.setUsername("now i have a username");
            userWithEmptyField.setEmail(null);
            exception = assertThrows(InvalidUserException.class, () -> userService.addUser(userWithEmptyField));

            Assertions.assertEquals("Email cannot be null.", exception.getMessage());
        }

        @Test
        void testGetAllUsersWithError() {
            when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));
            assertThrows(RuntimeException.class, () -> userService.getAllUsers());
        }


        @Test
        void testUpdateUserWithInvalidData() {
            User invalidUser = new User("username", null);
            assertThrows(InvalidUserException.class, () -> userService.updateUser(invalidUser));
        }

        @Test
        void testDeleteNonExistentUser() {
            Long userId = 1L;

            // Assuming findById will return null indicating no user found
            when(userService.findUserById(userId)).thenReturn(null);

            assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(userId));

            // Verify findById was called
            verify(userRepository).findById(userId);

            // Verify delete was never called since no user was found
            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
