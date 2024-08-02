package it.unifi.financeapp.gui;


import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.controller.UserController;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    // Assuming these are your service classes
    private final CategoryService categoryService;
    private final UserService userService;
    private final ExpenseService expenseService;
    JTabbedPane tabbedPane;

    MainFrame(CategoryService categoryService, UserService userService, ExpenseService expenseService) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.expenseService = expenseService;

        setTitle("Expense Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();

        // Create instances of the panels
        CategoryPanel categoryPanel = new CategoryPanel();
        UserPanel userPanel = new UserPanel();
        ExpensePanel expensePanel = new ExpensePanel();

        // Create instances of the controllers and connect them with views and services
        // Controllers
        CategoryController categoryController = new CategoryController(categoryService, categoryPanel);
        UserController userController = new UserController(userService, userPanel);
        ExpenseController expenseController = new ExpenseController(expenseService, categoryService, userService, expensePanel);

        // Initialize views within the controllers
        categoryController.initView();
        userController.initView();
        expenseController.initView();

        // Add the panels to the tabbed pane with labels
        tabbedPane.addTab("Categories", categoryPanel);
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.addTab("Expenses", expensePanel);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == tabbedPane.indexOfTab("Expenses")) {
                expenseController.updateData();
            }
        });

        // Add the tabbed pane to the main frame
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
