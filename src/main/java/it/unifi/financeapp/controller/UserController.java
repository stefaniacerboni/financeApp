package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.UserView;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.UserService;

public class UserController {
    private final UserService userService;
    private final UserView userView;

    public UserController(UserService userService, UserView userView) {
        this.userService = userService;
        this.userView = userView;
    }

    public void initView() {
        userView.getAddUserButton().addActionListener(e -> addUser());
        userView.getDeleteUserButton().addActionListener(e -> deleteUser());
        userView.getUserTable().getSelectionModel().addListSelectionListener(e -> updateDeleteButtonEnabledState());
        loadUsers();
    }

    void loadUsers() {
        java.util.List<User> users = userService.getAllUsers();
        users.forEach(userView::addUserToTable);
    }

    public void addUser() {
        User user = new User(userView.getUsername(), userView.getName(), userView.getSurname(), userView.getEmail());
        User result = userService.addUser(user);
        if (result != null) {
            userView.addUserToTable(result);
            userView.setStatus("User added successfully.");
            userView.clearForm();
        } else {
            userView.setStatus("Failed to add user.");
        }
    }

    public void deleteUser() {
        int selectedRow = userView.getSelectedUserIndex();
        if (selectedRow >= 0) {
            Long userId = userView.getUserIdFromTable(selectedRow);
            userService.deleteUser(userId);
            userView.removeUserFromTable(selectedRow);
            userView.setStatus("User deleted successfully.");
        } else {
            userView.setStatus("No user selected for deletion.");
        }
    }

    void updateDeleteButtonEnabledState() {
        boolean isSelected = userView.getSelectedUserIndex() >= 0;
        userView.getDeleteUserButton().setEnabled(isSelected);
    }
}
