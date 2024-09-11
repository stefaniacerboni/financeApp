package it.unifi.financeapp.gui;


import it.unifi.financeapp.controller.UserController;
import it.unifi.financeapp.model.User;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends BasePanel implements UserView {
    private JTextField usernameField;
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private UserController userController;

    @Override
    protected JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.setName("usernameField");
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        nameField.setName("nameField");
        formPanel.add(nameField);

        formPanel.add(new JLabel("Surname:"));
        surnameField = new JTextField();
        surnameField.setName("surnameField");
        formPanel.add(surnameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        emailField.setName("emailField");
        formPanel.add(emailField);

        addButton = createAddButton("User");
        formPanel.add(addButton);

        addButton.addActionListener(e -> userController.addUser());
        deleteButton.addActionListener(e -> userController.deleteUser());


        return formPanel;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Id", "Username", "Name", "Surname", "Email"};
    }

    @Override
    protected void attachDocumentListeners() {
        attachDocumentListeners(usernameField, emailField);
    }

    @Override
    protected void checkFields() {
        boolean enabled = !usernameField.getText().trim().isEmpty() && !emailField.getText().trim().isEmpty();
        addButton.setEnabled(enabled);
    }

    @Override
    public String getUsername() {
        return usernameField.getText();
    }

    @Override
    public void setUsername(String username) {
        usernameField.setText(username);
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    @Override
    public void setName(String name) {
        nameField.setText(name);
    }

    @Override
    public String getSurname() {
        return surnameField.getText();
    }

    @Override
    public void setSurname(String surname) {
        surnameField.setText(surname);
    }

    @Override
    public String getEmail() {
        return emailField.getText();
    }

    @Override
    public void setEmail(String email) {
        emailField.setText(email);
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void clearForm() {
        usernameField.setText("");
        nameField.setText("");
        surnameField.setText("");
        emailField.setText("");
    }

    @Override
    public void addUserToTable(User user) {
        tableModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getName(), user.getSurname(), user.getEmail()});
    }

    @Override
    public void removeUserFromTable(int rowIndex) {
        tableModel.removeRow(rowIndex);
    }

    @Override
    public int getSelectedUserIndex() {
        return entityTable.getSelectedRow();
    }

    @Override
    public Long getUserIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddUserButton() {
        return addButton;
    }

    @Override
    public JButton getDeleteUserButton() {
        return deleteButton;
    }

    @Override
    public JTable getUserTable() {
        return entityTable;
    }
}
