package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.ExpenseView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.model.Expense;
import it.unifi.financeapp.model.User;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {
    @Mock
    private ExpenseService expenseService;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ExpenseView expenseView;
    @Mock
    private JButton addExpenseButton;
    @Mock
    private JButton deleteExpenseButton;
    @Mock
    private JTable expenseTable;
    @Mock
    private ListSelectionModel selectionModel;
    @Mock
    private JComboBox<User> userComboBox;
    @Mock
    private JComboBox<Category> categoryComboBox;

    @InjectMocks
    private ExpenseController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(expenseView.getAddExpenseButton()).thenReturn(addExpenseButton);
        controller = new ExpenseController(expenseService, categoryService, userService, expenseView);
        controller.initView();
    }

    @Test
    void testInitializeView() {
        verify(expenseService).getAllExpenses();
        verify(expenseView).getAddExpenseButton();
        verify(expenseView).getDeleteExpenseButton();
    }

    @Test
    void testLoadExpenses() {
        Category category = new Category("Tech", "Tech stuff");
        User user = new User("JohnDoe", "John", "Doe", "john.doe@example.com");
        List<Expense> expenses = Arrays.asList(
                new Expense(category, user, 30.0, "2024-12-12"),
                new Expense(category, user, 50.0, "2024-12-24"));
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        controller.loadExpenses();

        verify(expenseView, times(expenses.size())).addExpenseToTable(any(Expense.class));
    }

    @Test
    void testAddExpense() {
        User user = new User("JohnDoe", "John", "Doe", "john.doe@example.com");
        Category category = new Category("Travel", "Travel expenses");
        when(expenseView.getUserComboBox()).thenReturn(userComboBox);
        when(expenseView.getCategoryComboBox()).thenReturn(categoryComboBox);
        when(expenseView.getUserComboBox().getSelectedItem()).thenReturn(user);
        when(expenseView.getCategoryComboBox().getSelectedItem()).thenReturn(category);
        when(expenseView.getAmount()).thenReturn("100");
        when(expenseView.getDate()).thenReturn("2024-01-01");
        Expense expense = new Expense(category, user, 100L, "2024-01-01");
        when(expenseService.addExpense(any(Expense.class))).thenReturn(expense);

        controller.addExpense();

        verify(expenseService).addExpense(any(Expense.class));
        verify(expenseView).addExpenseToTable(expense);
        verify(expenseView).setStatus("Expense added successfully.");
        verify(expenseView).clearForm();
    }

    @Test
    void testHandleAddExpenseFailure() {
        User user = new User("JohnDoe", "John", "Doe", "john.doe@example.com");
        Category category = new Category("Travel", "Travel expenses");
        when(expenseView.getUserComboBox()).thenReturn(userComboBox);
        when(expenseView.getCategoryComboBox()).thenReturn(categoryComboBox);
        when(expenseView.getUserComboBox().getSelectedItem()).thenReturn(user);
        when(expenseView.getCategoryComboBox().getSelectedItem()).thenReturn(category);
        when(expenseView.getAmount()).thenReturn("100");
        when(expenseView.getDate()).thenReturn("2024-01-01");
        when(expenseService.addExpense(any(Expense.class))).thenReturn(null);

        controller.addExpense();

        verify(expenseView).setStatus("Failed to add expense.");
    }

    @Test
    void testDeleteExpense() {
        when(expenseView.getSelectedExpenseIndex()).thenReturn(0);
        when(expenseView.getExpenseIdFromTable(0)).thenReturn(1L);

        controller.deleteExpense();

        verify(expenseService).deleteExpense(1L);
        verify(expenseView).removeExpenseFromTable(0);
        verify(expenseView).setStatus("Expense deleted successfully.");
    }

    @Test
    void testNotDeleteExpenseWhenNoneSelected() {
        when(expenseView.getSelectedExpenseIndex()).thenReturn(-1);

        controller.deleteExpense();

        verify(expenseView, never()).getExpenseIdFromTable(anyInt());
        verify(expenseService, never()).deleteExpense(anyLong());
        verify(expenseView).setStatus("No expense selected for deletion.");
    }

    @Test
    void testUpdateDeleteButtonEnabledState() {
        when(expenseView.getSelectedExpenseIndex()).thenReturn(0);
        controller.updateDeleteButtonEnabledState();
        verify(deleteExpenseButton).setEnabled(true);

        when(expenseView.getSelectedExpenseIndex()).thenReturn(-1);
        controller.updateDeleteButtonEnabledState();
        verify(deleteExpenseButton).setEnabled(false);
    }

    @Test
    void testUpdateData() {
        when(expenseView.getUserComboBox()).thenReturn(userComboBox);
        when(expenseView.getCategoryComboBox()).thenReturn(categoryComboBox);
        controller.updateData();
        verify(userService).getAllUsers();
        verify(categoryService).getAllCategories();
    }

    @Test
    void testPopulateUserComboBoxWhenUsersAreUpdated() {
        when(expenseView.getUserComboBox()).thenReturn(userComboBox);

        List<User> users = List.of(new User("User1", "Email1"), new User("User2", "Email2"));
        when(userService.getAllUsers()).thenReturn(users);

        controller.updateUsers();

        verify(userComboBox).setModel(any(DefaultComboBoxModel.class));
    }
}
