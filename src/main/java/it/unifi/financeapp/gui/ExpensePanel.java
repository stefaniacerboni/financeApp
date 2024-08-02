package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ExpensePanel extends JPanel implements ExpenseView {
    private JComboBox<User> userComboBox;
    private JComboBox<Category> categoryComboBox;
    private JTextField amountField, dateField;
    private JButton addExpenseButton, deleteExpenseButton;
    private JTable expenseTable;
    private JLabel statusLabel;
    private DefaultTableModel tableModel;

    public ExpensePanel() {
        setLayout(new BorderLayout());
        initUI();
    }

    void initUI() {
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

        addExpenseButton = new JButton("Add Expense");
        addExpenseButton.setEnabled(false); // Initially disable the Add button
        addExpenseButton.setName("addExpenseButton");
        formPanel.add(addExpenseButton);

        // Table to display categories
        String[] columnNames = {"Id", "User", "Category", "Amount", "Date"};
        Object[][] data = {};  // Initial empty data
        tableModel = new DefaultTableModel(data, columnNames);
        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setName("expenseTable");


        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setName("expenseScrollPane");
        scrollPane.setPreferredSize(new Dimension(400, 150));

        // Button to delete selected category
        deleteExpenseButton = new JButton("Delete Selected");
        deleteExpenseButton.setName("deleteExpenseButton");
        deleteExpenseButton.setEnabled(false);

        statusLabel = new JLabel(" ");
        statusLabel.setName("statusLabel");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel to hold the button and label
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.setName("southPanel");
        southPanel.add(deleteExpenseButton);
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
        amountField.getDocument().addDocumentListener(listener);
        dateField.getDocument().addDocumentListener(listener);

        expenseTable.getSelectionModel().addListSelectionListener(e -> updateDeleteButtonEnabledState());
    }

    /**
     * Enables the Add button only if both amount and date fields are not empty.
     */
    void checkFields() {
        String amount = amountField.getText();
        String date = dateField.getText();
        addExpenseButton.setEnabled(!amount.trim().isEmpty() && !date.trim().isEmpty());

    }

    void updateDeleteButtonEnabledState() {
        boolean isSelected = getSelectedExpenseIndex() >= 0;
        deleteExpenseButton.setEnabled(isSelected);
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
        return expenseTable.getSelectedRow();
    }

    @Override
    public Long getExpenseIdFromTable(int rowIndex) {
        return (Long) tableModel.getValueAt(rowIndex, 0);
    }

    @Override
    public JButton getAddExpenseButton() {
        return addExpenseButton;
    }

    @Override
    public JButton getDeleteExpenseButton() {
        return deleteExpenseButton;
    }

    @Override
    public JTable getExpenseTable() {
        return expenseTable;
    }
}

