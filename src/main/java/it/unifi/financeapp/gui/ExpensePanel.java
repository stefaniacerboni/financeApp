package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;

import javax.swing.*;
import java.awt.*;

public class ExpensePanel extends BasePanel implements ExpenseView {

	private static final long serialVersionUID = 1L;

	private JComboBox<User> userComboBox;
	private JComboBox<Category> categoryComboBox;
	private JTextField amountField;
	private JTextField dateField;
	private transient ExpenseController expenseController;

	@Override
	protected JPanel createFormPanel() {
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
		amountField.setName("Amount");
		formPanel.add(amountField);

		formPanel.add(new JLabel("Date (yyyy-mm-dd):"));
		dateField = new JTextField();
		dateField.setName("Date");
		formPanel.add(dateField);

		addButton = createAddButton("Expense");
		formPanel.add(addButton);

		deleteButton.setText("Delete Expense");

		addButton.addActionListener(e -> expenseController.addExpense());
		deleteButton.addActionListener(e -> expenseController.deleteExpense());

		return formPanel;
	}

	public void setExpenseController(ExpenseController expenseController) {
		this.expenseController = expenseController;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { "Id", "User", "Category", "Amount", "Date" };
	}

	@Override
	protected void attachDocumentListeners() {
		attachDocumentListeners(amountField, dateField);
	}

	/**
	 * Enables the Add button only if both amount and date fields are not empty.
	 */
	@Override
	protected void checkFields() {
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
		tableModel.addRow(new Object[] { expense.getId(), expense.getUser().getUsername(),
				expense.getCategory().getName(), expense.getAmount(), expense.getDate() });
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
