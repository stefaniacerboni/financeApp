package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.ExpenseController;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpensePanelTest {
    @Mock
    private ExpenseController expenseController;

    private FrameFixture window;

    private ExpensePanel expenseView;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            expenseView = new ExpensePanel();
            expenseView.setExpenseController(expenseController);
            // Mock data for JComboBoxes
            expenseView.getUserComboBox().addItem(new User("User1", "Email1"));  // Mock user
            expenseView.getCategoryComboBox().addItem(new Category("Category1", "Description1"));  // Mock category
            f.setContentPane(expenseView);
            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return f;
        });
        window = new FrameFixture(frame);
        window.show(); // shows the frame to test
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
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
    void testTextFieldContentIsMatching() {
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
        JTableFixture entityTable = window.table("entityTable");
        entityTable.requireRowCount(0);
        assertEquals(-1, expenseView.getSelectedExpenseIndex());

    }

    @Test
    void testEnableAddButtonWhenFieldsAreFilled() {
        window.comboBox("userComboBox").selectItem(0);
        window.comboBox("categoryComboBox").selectItem(0);
        window.textBox("amountField").setText("100");
        window.textBox("dateField").setText("2024-01-01");
        window.button("addButton").requireEnabled();
        assertTrue(expenseView.getAddExpenseButton().isEnabled());
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

    private Expense addExpenseToTable() {
        User user = (User) expenseView.getUserComboBox().getSelectedItem();
        Category category = (Category) expenseView.getCategoryComboBox().getSelectedItem();
        Expense expense = new Expense(category, user, 100.0, "2024-01-01");
        expense.setId(1L);
        execute(() -> expenseView.addExpenseToTable(expense));
        return expense;
    }

    @Test
    void testShownExpenseShouldMatchExpenseAdded() {
        Expense expense = addExpenseToTable();
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
        addExpenseToTable();
        // Select the first row and assert that the delete button is enabled
        execute(() -> expenseView.getExpenseTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();
        assertTrue(expenseView.getDeleteExpenseButton().isEnabled());

        // Clear selection and assert that the delete button is disabled
        execute(() -> expenseView.getExpenseTable().clearSelection());
        window.button("deleteButton").requireDisabled();
        assertFalse(expenseView.getDeleteExpenseButton().isEnabled());
    }

    @Test
    void testDeleteButtonShouldRemoveExpenseFromTable() {
        addExpenseToTable();
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


    @Test
    void testAddExpenseShouldDelegateToExpenseController() {
        JTextComponentFixture amountField = window.textBox("amountField");
        JTextComponentFixture dateField = window.textBox("dateField");
        amountField.setText("100");
        dateField.setText("2024-01-01");
        execute(() -> window.button(JButtonMatcher.withName("addButton")).target().doClick());
        verify(expenseController).addExpense();
    }

    @Test
    void testDeleteExpenseShouldDelegateToExpenseController() {
        addExpenseToTable();
        execute(() -> expenseView.getExpenseTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();
        execute(() -> window.button(JButtonMatcher.withName("deleteButton")).target().doClick());
        verify(expenseController).deleteExpense();
    }
}
