package it.unifi.financeapp.gui;

import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MainFrameTest {

    private FrameFixture window;

    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private ExpenseService expenseService;

    @BeforeEach
    public void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> new MainFrame(categoryService, userService, expenseService));
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void shouldContainTabsWithProperTitles() {
        window.tabbedPane().requireVisible();
        window.tabbedPane().requireTabTitles("Categories", "Users", "Expenses");
    }

    @Test
    public void switchingToExpensesTabShouldTriggerDataLoading() {
        // Reset mocks to clear any interactions during initialization
        Mockito.reset(categoryService, userService, expenseService);

        // Interact with the UI
        window.tabbedPane().target().setSelectedIndex(2);

        // Verify that the expected method was called as a result of the interaction
        verify(categoryService, atLeastOnce()).getAllCategories();
        verify(userService, atLeastOnce()).getAllUsers();
    }

    @Test
    public void switchingToCategoriesTabShouldNotTriggerDataLoading() {
        // Reset mocks to clear any interactions during initialization
        Mockito.reset(categoryService, userService, expenseService);

        // Interact with the UI
        window.tabbedPane().target().setSelectedIndex(0);

        // Verify that the expected method was called as a result of the interaction
        verify(expenseService, never()).getAllExpenses();
        verify(categoryService, never()).getAllCategories();
        verify(userService, never()).getAllUsers();
    }
}
