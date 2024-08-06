package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpensePanelTest {

    private FrameFixture window;
    private ExpenseView expenseView;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            expenseView = new ExpensePanel();
            // Mock data for JComboBoxes
            expenseView.getUserComboBox().addItem(new User("User1", "Email1"));  // Mock user
            expenseView.getCategoryComboBox().addItem(new Category("Category1", "Description1"));  // Mock category
            f.setContentPane((Container) expenseView);
            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return f;
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void testInitialConditions() {
        window.textBox("amountField").requireEmpty();
        window.textBox("dateField").requireEmpty();
        window.button("addButton").requireDisabled();
        window.button("deleteButton").requireDisabled();
        window.table("entityTable").requireRowCount(0);
    }

    @Test
    void testTextFieldContentIsMatching(){
        JTextComponentFixture amountField = window.textBox("amountField");
        JTextComponentFixture dateField = window.textBox("dateField");
        amountField.setText("Name");
        dateField.setText("Description");
        amountField.requireText(expenseView.getAmount());
        dateField.requireText(expenseView.getDate());
        expenseView.setAmount("New Name");
        expenseView.setDate("New Description");
        amountField.requireText(expenseView.getAmount());
        dateField.requireText(expenseView.getDate());
    }

    @Test
    void testEnableAddButtonWhenFieldsAreFilled() {
        window.comboBox("userComboBox").selectItem(0);
        window.comboBox("categoryComboBox").selectItem(0);
        window.textBox("amountField").setText("100");
        window.textBox("dateField").setText("2024-01-01");
        window.button("addButton").requireEnabled();
    }

    @Test
    void testWhenEitherNameOrDescriptionAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture amountField = window.textBox("amountField");
        JTextComponentFixture dateField = window.textBox("dateField");
        amountField.setText("100");
        dateField.setText(" ");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        amountField.setText("");
        dateField.setText("");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        amountField.setText(" ");
        dateField.setText("2024-01-01");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
    }

    @Test
    void testStatusUpdateAfterAddingCategory() {
        execute(() -> expenseView.setStatus("Expense added successfully"));
        JLabelFixture statusLabel = window.label("statusLabel");
        statusLabel.requireText("Expense added successfully");
    }

    @Test
    void testShownExpenseShouldMatchExpenseAdded() {
        User user = (User) expenseView.getUserComboBox().getSelectedItem();
        Category category = (Category) expenseView.getCategoryComboBox().getSelectedItem();
        Expense expense = new Expense(category, user, 100.0, "2024-01-01");
        expense.setId(1L);
        execute(() -> expenseView.addExpenseToTable(expense));
        DefaultTableModel model = (DefaultTableModel) expenseView.getExpenseTable().getModel();
        assertEquals(1, model.getRowCount(), "Table should have one row after adding a category");
        assertEquals(expense.getId(), model.getValueAt(0, 0), "Expense Id in the table should match the added expense");
        assertEquals(expense.getUser().getUsername(), model.getValueAt(0, 1), "Expense username in the table should match the added expense");
        assertEquals(expense.getCategory().getName(), model.getValueAt(0, 2), "Expense category name in the table should match the added expense");
        assertEquals(expense.getAmount(), model.getValueAt(0, 3), "Expense amount in the table should match the added expense");
        assertEquals(expense.getDate(), model.getValueAt(0, 4), "Expense date name in the table should match the added expense");
    }

    @Test
    void testDeleteButtonShouldBeEnabledOnlyWhenAnExpenseIsSelected() {
        testShownExpenseShouldMatchExpenseAdded();
        // Select the first row and assert that the delete button is enabled
        execute(() -> expenseView.getExpenseTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();

        // Clear selection and assert that the delete button is disabled
        execute(() -> expenseView.getExpenseTable().clearSelection());
        window.button("deleteButton").requireDisabled();
    }

    @Test
    void testDeleteButtonShouldRemoveExpenseFromTable() {
        testShownExpenseShouldMatchExpenseAdded();
        JTableFixture tableFixture = window.table("entityTable");
        tableFixture.requireRowCount(1);
        // Select the first row and assert that the delete button is enabled
        execute(() -> expenseView.getExpenseTable().setRowSelectionInterval(0, 0));
        assertEquals(1L, expenseView.getExpenseIdFromTable(0));
        window.button("deleteButton").requireEnabled();
        execute(() -> expenseView.removeExpenseFromTable(0));
        tableFixture.requireRowCount(0);
    }

    @Test
    void testClearFormShouldClearTextFields() {
        JTextComponentFixture amountField = window.textBox("amountField");
        JTextComponentFixture dateField = window.textBox("dateField");

        amountField.setText("100");
        dateField.setText("2024-01-01");
        execute(() -> expenseView.clearForm());

        amountField.requireText("");
        dateField.requireText("");
    }
}
