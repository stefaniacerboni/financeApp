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
        when(expenseView.getDeleteExpenseButton()).thenReturn(deleteExpenseButton);
        when(expenseView.getExpenseTable()).thenReturn(expenseTable);
        when(expenseTable.getSelectionModel()).thenReturn(selectionModel);
        controller = new ExpenseController(expenseService, categoryService, userService, expenseView);
        controller.initView();
    }

    @Test
    void shouldInitializeView() {
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
}
