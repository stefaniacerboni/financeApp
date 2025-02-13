package it.unifi.financeapp.controller;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.financeapp.gui.UserView;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.UserService;
import it.unifi.financeapp.service.exceptions.InvalidUserException;

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

	@Nested
	@DisplayName("Happy Cases")
	class HappyCases {

		@Test
		void shouldInitializeView() {
			verify(userService).getAllUsers(); // loadUsers() is called in initView()
		}

		@Test
		void testLoadUsersOnInit() {
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
		void testNewUserConcurrent() {
			List<User> users = new ArrayList<>();
			User user = new User("Username", "Name", "Surname", "Email");
			doAnswer(invocation -> {
				users.add(user);
				return null;
			}).when(userService).addUser(any(User.class));
			when(userView.getUsername()).thenReturn("Username");
			when(userView.getName()).thenReturn("Name");
			when(userView.getSurname()).thenReturn("Surname");
			when(userView.getEmail()).thenReturn("Email");
			List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> controller.addUser()))
					.peek(Thread::start).collect(Collectors.toList());
			await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
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

	@Nested
	@DisplayName("Bad Cases")
	class BadCases {
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
		void testNotDeleteIfUserHasDependencies() {
			Long id = 1L;
			when(userView.getSelectedUserIndex()).thenReturn(0);
			when(userView.getUserIdFromTable(0)).thenReturn(id);
			doThrow(new InvalidUserException("Cannot delete user with existing expenses")).when(userService)
					.deleteUser(id);

			controller.deleteUser();

			verify(userView).setStatus("Cannot delete user with existing expenses");
		}
	}
}