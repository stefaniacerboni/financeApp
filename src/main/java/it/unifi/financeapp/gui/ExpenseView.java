package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;

import javax.swing.*;

public interface ExpenseView {
	JComboBox<User> getUserComboBox();

	JComboBox<Category> getCategoryComboBox();

	String getAmount();

	void setAmount(String amount);

	String getDate();

	void setDate(String date);

	void setStatus(String status);

	void clearForm();

	void addExpenseToTable(Expense expense);

	void removeExpenseFromTable(int rowIndex);

	int getSelectedExpenseIndex();

	Long getExpenseIdFromTable(int rowIndex);

	JButton getAddExpenseButton();

	JButton getDeleteExpenseButton();

	JTable getExpenseTable();
}