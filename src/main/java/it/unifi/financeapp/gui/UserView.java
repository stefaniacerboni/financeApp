package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.User;

import javax.swing.*;

public interface UserView {
    String getUsername();

    void setUsername(String username);

    String getName();

    void setName(String name);

    String getSurname();

    void setSurname(String surname);

    String getEmail();

    void setEmail(String email);

    void setStatus(String status);

    void clearForm();

    void addUserToTable(User user);

    void removeUserFromTable(int rowIndex);

    int getSelectedUserIndex();

    Long getUserIdFromTable(int rowIndex);

    JButton getAddUserButton();

    JButton getDeleteUserButton();

    JTable getUserTable();
}
