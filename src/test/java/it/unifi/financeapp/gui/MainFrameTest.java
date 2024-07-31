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
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

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
        // Assuming that switching to the "Expenses" tab triggers data loading
        window.tabbedPane().selectTab("Expenses");
        // Verify that data loading method was called
        verify(categoryService, atLeastOnce()).getAllCategories();
        verify(userService, atLeastOnce()).getAllUsers();
        verify(expenseService, atLeastOnce()).getAllExpenses();
    }
}
