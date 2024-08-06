package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;

import javax.swing.*;
import java.awt.*;

public class ExpensePanel extends BasePanel implements ExpenseView {
    private JComboBox<User> userComboBox;
    private JComboBox<Category> categoryComboBox;
    private JTextField amountField;
    private JTextField dateField;

    public ExpensePanel() {
    }

    public JPanel createFormPanel() {
        // Top Panel for form
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.setName("formPanel");
        formPanel.add(new JLabel("User:"));
        userComboBox = new JComboBox<>();
        userComboBox.setName("userComboBox");
        formPanel.add(userComboBox);

        formPanel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setName("categoryComboBox");
        formPanel.add(categoryComboBox);

        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        amountField.setName("amountField");
        formPanel.add(amountField);

        formPanel.add(new JLabel("Date (yyyy-mm-dd):"));
        dateField = new JTextField();
        dateField.setName("dateField");
        formPanel.add(dateField);

        addButton = new JButton("Add Expense");
        addButton.setEnabled(false); // Initially disable the Add button
        addButton.setName("addExpenseButton");
        formPanel.add(addButton);

        return formPanel;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[]{"Id", "User", "Category", "Amount", "Date"};
    }

    @Override
    public void attachDocumentListeners() {
        amountField.getDocument().addDocumentListener(listener);
        dateField.getDocument().addDocumentListener(listener);

        entityTable.getSelectionModel().addListSelectionListener(e -> updateDeleteButtonEnabledState());
    }

    /**
     * Enables the Add button only if both amount and date fields are not empty.
     */
    @Override
    public void checkFields() {
        String amount = amountField.getText();
        String date = dateField.getText();
        addButton.setEnabled(!amount.trim().isEmpty() && !date.trim().isEmpty());

    }

    @Override
    public JComboBox<User> getUserComboBox() {
        return userComboBox;
    }

    @Override
    public JComboBox<Category> getCategoryComboBox() {
        return categoryComboBox;
    }

    @Override
    public String getAmount() {
        return amountField.getText();
    }

    @Override
    public void setAmount(String amount) {
        amountField.setText(amount);
    }

    @Override
    public String getDate() {
        return dateField.getText();
    }

    @Override
    public void setDate(String date) {
        dateField.setText(date);
    }

    @Override
    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    @Override
    public void clearForm() {
        amountField.setText("");
        dateField.setText("");
    }

    @Override
    public void addExpenseToTable(Expense expense) {
        tableModel.addRow(new Object[]{expense.getId(), expense.getUser().getUsername(), expense.getCategory().getName(), expense.getAmount(), expense.getDate()});
    }

    @Override
    public void removeExpenseFromTable(int rowIndex) {
        tableModel.removeRow(rowIndex);
    }

    @Override
    public int getSelectedExpenseIndex() {
        return entityTable.getSelectedRow();
    }

    @Override
    public Long getExpenseIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddExpenseButton() {
        return addButton;
    }

    @Override
    public JButton getDeleteExpenseButton() {
        return deleteButton;
    }

    @Override
    public JTable getExpenseTable() {
        return entityTable;
    }
}

