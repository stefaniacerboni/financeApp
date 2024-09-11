package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.UserView;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserView userView;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(userService, userView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
        verify(userService).getAllUsers();  // loadUsers() is called in initView()
    }

    @Test
    void testLoadUsers() {
        List<User> users = Arrays.asList(new User("Username", "Email"), new User("Username2", "Email2"));
        when(userService.getAllUsers()).thenReturn(users);

        controller.loadUsers();

        verify(userView, times(users.size())).addUserToTable(any(User.class));
    }

    @Test
    void testAddUserSuccessfully() {
        when(userView.getUsername()).thenReturn("JohnDoe");
        when(userView.getName()).thenReturn("John");
        when(userView.getSurname()).thenReturn("Doe");
        when(userView.getEmail()).thenReturn("johndoe@example.com");
        User newUser = new User("JohnDoe", "John", "Doe", "johndoe@example.com");
        when(userService.addUser(any(User.class))).thenReturn(newUser);

        controller.addUser();

        verify(userService).addUser(newUser);
        verify(userView).addUserToTable(newUser);
        verify(userView).setStatus("User added successfully.");
        verify(userView).clearForm();
    }

    @Test
    void testAddUserFailure() {
        when(userView.getUsername()).thenReturn("JohnDoe");
        when(userView.getName()).thenReturn("John");
        when(userView.getSurname()).thenReturn("Doe");
        when(userView.getEmail()).thenReturn("johndoe@example.com");
        when(userService.addUser(any(User.class))).thenReturn(null);

        controller.addUser();

        verify(userView).setStatus("Failed to add user.");
    }

    @Test
    void testNotDeleteIfNoUserSelected() {
        when(userView.getSelectedUserIndex()).thenReturn(-1);

        controller.deleteUser();

        verify(userView, never()).getUserIdFromTable(anyInt());
        verify(userService, never()).deleteUser(anyLong());
        verify(userView).setStatus("No user selected for deletion.");
    }

    @Test
    void testDeleteSelectedUser() {
        when(userView.getSelectedUserIndex()).thenReturn(0);
        when(userView.getUserIdFromTable(0)).thenReturn(1L);

        controller.deleteUser();

        verify(userService).deleteUser(1L);
        verify(userView).removeUserFromTable(0);
        verify(userView).setStatus("User deleted successfully.");
    }
}
