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
class MainFrameTest {

    MainFrame mainFrame;
    private FrameFixture window;
    @Mock
    private CategoryService categoryService;
    @Mock
    private UserService userService;
    @Mock
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            mainFrame = new MainFrame(categoryService, userService, expenseService);
            mainFrame.pack();
            mainFrame.validate();
            mainFrame.repaint();
            mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return mainFrame;
        });
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        window.cleanUp();
    }

    @Test
    void shouldContainTabsWithProperTitles() {
        window.tabbedPane().requireVisible();
        window.tabbedPane().requireTabTitles("Categories", "Users", "Expenses");
    }

    @Test
    void switchingToExpensesTabShouldTriggerDataLoading() {
        // Reset mocks to clear any interactions during initialization
        Mockito.reset(categoryService, userService, expenseService);

        // Interact with the UI
        mainFrame.getTabbedPane().setSelectedIndex(2);

        // Verify that the expected method was called as a result of the interaction
        verify(categoryService, atLeastOnce()).getAllCategories();
        verify(userService, atLeastOnce()).getAllUsers();
    }

    @Test
    void switchingToCategoriesTabShouldNotTriggerDataLoading() {
        // Reset mocks to clear any interactions during initialization
        Mockito.reset(categoryService, userService, expenseService);

        // Interact with the UI
        mainFrame.getTabbedPane().setSelectedIndex(0);

        // Verify that the expected method was called as a result of the interaction
        verify(expenseService, never()).getAllExpenses();
        verify(categoryService, never()).getAllCategories();
        verify(userService, never()).getAllUsers();

        mainFrame.getTabbedPane().setSelectedIndex(1);

        verify(expenseService, never()).getAllExpenses();
        verify(categoryService, never()).getAllCategories();
        verify(userService, never()).getAllUsers();

    }
}