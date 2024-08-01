package it.unifi.financeapp.gui;


import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.controller.UserController;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    //Controller
    private CategoryController categoryController;
    private UserController userController;
    private ExpenseController expenseController;

    //Panel
    private CategoryPanel categoryPanel;
    private UserPanel userPanel;
    private ExpensePanel expensePanel;


    public MainFrame(CategoryController categoryController, UserController userController, ExpenseController expenseController) {
        this.categoryController = categoryController;
        this.userController = userController;
        this.expenseController = expenseController;

        setTitle("Expense Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    void initUI() {
        tabbedPane = new JTabbedPane();

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
}
