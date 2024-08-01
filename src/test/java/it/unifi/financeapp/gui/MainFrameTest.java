package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.controller.ExpenseController;
import it.unifi.financeapp.controller.UserController;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainFrameTest {

    @Mock
    private CategoryController categoryController;
    @Mock
    private UserController userController;
    @Mock
    private ExpenseController expenseController;

    private FrameFixture window;

    @BeforeEach
    public void setUp() {
        // Note: Ensure mocks are correctly set to return panels before creating MainFrame
        JFrame frame = GuiActionRunner.execute(() -> new MainFrame(categoryController, userController, expenseController));
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
        verify(expenseController, atLeastOnce()).updateData();
    }

    @Test
    public void switchingToCategoriesTabShouldNotTriggerDataLoading() {
        // Switching to the "Category" tab shouldn't trigger data loading
        window.tabbedPane().selectTab("Categories");
        // Verify that data loading method wasn't called
        verify(expenseController, never()).updateData();
    }
}
