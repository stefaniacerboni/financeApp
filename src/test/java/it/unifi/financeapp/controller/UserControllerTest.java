package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.UserView;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserView userView;
    @Mock
    private JButton addUserButton;
    @Mock
    private JButton deleteUserButton;
    @Mock
    private JTable userTable;
    @Mock
    private ListSelectionModel selectionModel;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(userView.getAddUserButton()).thenReturn(addUserButton);
        when(userView.getDeleteUserButton()).thenReturn(deleteUserButton);
        when(userView.getUserTable()).thenReturn(userTable);
        when(userTable.getSelectionModel()).thenReturn(selectionModel);
        controller = new UserController(userService, userView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
        verify(userView).getAddUserButton();
        verify(userView).getDeleteUserButton();
        verify(userService).getAllUsers();  // Assuming loadUsers() is called in initView()
    }

    @Test
    void testLoadUsers() {
        List<User> users = Arrays.asList(new User("Username", "Email"), new User("Username2", "Email2"));
        when(userService.getAllUsers()).thenReturn(users);

        controller.loadUsers();

        verify(userView, times(users.size())).addUserToTable(any(User.class));
    }

    @Test
    void shouldAddUserSuccessfully() {
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
}
