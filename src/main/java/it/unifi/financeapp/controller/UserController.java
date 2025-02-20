package it.unifi.financeapp.controller;

import org.hibernate.service.spi.ServiceException;

import it.unifi.financeapp.gui.UserView;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.UserService;
import it.unifi.financeapp.service.exceptions.InvalidUserException;

public class UserController {
	private final UserService userService;
	private final UserView userView;

	public UserController(UserService userService, UserView userView) {
		this.userService = userService;
		this.userView = userView;
	}

	public void initView() {
		loadUsers();
	}

	void loadUsers() {
		java.util.List<User> users = userService.getAllUsers();
		users.forEach(userView::addUserToTable);
	}

	public void addUser() {
		User user = new User(userView.getUsername(), userView.getName(), userView.getSurname(), userView.getEmail());
		try {
			User result = userService.addUser(user);
			if (result != null) {
				userView.addUserToTable(result);
				userView.setStatus("User added successfully.");
				userView.clearForm();
			} else {
				userView.setStatus("Failed to add user.");
			}
		} catch (ServiceException se) {
			userView.setStatus("Failed to add user: Persistence error.");

		}
	}

	public void deleteUser() {
		int selectedRow = userView.getSelectedUserIndex();
		if (selectedRow >= 0) {
			Long userId = userView.getUserIdFromTable(selectedRow);
			try {
				userService.deleteUser(userId);
				userView.removeUserFromTable(selectedRow);
				userView.setStatus("User deleted successfully.");
			} catch (InvalidUserException userException) {
				userView.setStatus(userException.getMessage());
			}
		} else {
			userView.setStatus("No user selected for deletion.");
		}
	}
}