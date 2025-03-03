package it.unifi.financeapp.controller;


import it.unifi.financeapp.gui.ExpenseView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ExpenseView expenseView;

    public ExpenseController(ExpenseService expenseService, CategoryService categoryService, UserService userService, ExpenseView expenseView) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.expenseView = expenseView;
    }

    public void initView() {
        loadExpenses();
    }

    public void loadExpenses() {
        java.util.List<Expense> expenses = expenseService.getAllExpenses();
        expenses.forEach(expenseView::addExpenseToTable);
    }

    public void addExpense() {
        User user = (User) expenseView.getUserComboBox().getSelectedItem();
        Category category = (Category) expenseView.getCategoryComboBox().getSelectedItem();
        Expense expense = new Expense(category, user, Long.parseLong(expenseView.getAmount()), expenseView.getDate());
        Expense result = expenseService.addExpense(expense);
        if (result != null) {
            expenseView.addExpenseToTable(result);
            expenseView.setStatus("Expense added successfully.");
            expenseView.clearForm();
        } else {
            expenseView.setStatus("Failed to add expense.");
        }
    }

    public void deleteExpense() {
        int selectedRow = expenseView.getSelectedExpenseIndex();
        if (selectedRow >= 0) {
            Long expenseId = expenseView.getExpenseIdFromTable(selectedRow);
            expenseService.deleteExpense(expenseId);
            expenseView.removeExpenseFromTable(selectedRow);
            expenseView.setStatus("Expense deleted successfully.");
        } else {
            expenseView.setStatus("No expense selected for deletion.");
        }
    }

    public void updateData() {
        updateUsers();
        updateCategories();
    }

    void updateUsers() {
        List<User> users = userService.getAllUsers();
        expenseView.getUserComboBox().setModel(new DefaultComboBoxModel<>(new Vector<>(users)));
    }

    void updateCategories() {
        List<Category> categories = categoryService.getAllCategories();
        expenseView.getCategoryComboBox().setModel(new DefaultComboBoxModel<>(new Vector<>(categories)));
    }
}