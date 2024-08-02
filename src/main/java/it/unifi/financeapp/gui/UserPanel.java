package it.unifi.financeapp.gui;


import it.unifi.financeapp.model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserPanel extends JPanel implements UserView {
    private JTextField usernameField, nameField, surnameField, emailField;
    private JButton addUserButton, deleteUserButton;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;


    public UserPanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    void initUI() {
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

        addUserButton = new JButton("Add User");
        addUserButton.setEnabled(false);
        addUserButton.setName("addUserButton");
        formPanel.add(addUserButton);

        String[] columnNames = {"Id", "Username", "Name", "Surname", "Email"};
        tableModel = new DefaultTableModel(null, columnNames);
        userTable = new JTable(tableModel);
        userTable.setName("userTable");
        JScrollPane scrollPane = new JScrollPane(userTable);

        deleteUserButton = new JButton("Delete Selected");
        deleteUserButton.setEnabled(false);
        deleteUserButton.setName("deleteUserButton");
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.add(deleteUserButton);

        statusLabel = new JLabel(" ");
        statusLabel.setName("statusLabel");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(statusLabel);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        attachDocumentListeners();
    }

    private void attachDocumentListeners() {
        DocumentListener listener = new DocumentListener() {
            @Generated
            public void changedUpdate(DocumentEvent e) {
                // This method is not used in this context.
            }

            public void removeUpdate(DocumentEvent e) {
                checkFields();
            }

            public void insertUpdate(DocumentEvent e) {
                checkFields();
            }
        };
        usernameField.getDocument().addDocumentListener(listener);
        emailField.getDocument().addDocumentListener(listener);

        userTable.getSelectionModel().addListSelectionListener(e -> updateDeleteButtonEnabledState());
    }

    void checkFields() {
        boolean enabled = !usernameField.getText().trim().isEmpty() && !emailField.getText().trim().isEmpty();
        addUserButton.setEnabled(enabled);
    }


    void updateDeleteButtonEnabledState() {
        boolean isSelected = getSelectedUserIndex() >= 0;
        deleteUserButton.setEnabled(isSelected);
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
        return userTable.getSelectedRow();
    }

    @Override
    public Long getUserIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddUserButton() {
        return addUserButton;
    }

    @Override
    public JButton getDeleteUserButton() {
        return deleteUserButton;
    }

    @Override
    public JTable getUserTable() {
        return userTable;
    }
}
