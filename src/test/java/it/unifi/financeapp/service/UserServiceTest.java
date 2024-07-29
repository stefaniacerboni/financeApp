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
}
