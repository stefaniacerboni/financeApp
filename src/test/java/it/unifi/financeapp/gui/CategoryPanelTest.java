package it.unifi.financeapp.gui;

import it.unifi.financeapp.model.Category;
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

class CategoryPanelTest {

    private FrameFixture window;

    private CategoryView categoryView;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            categoryView = new CategoryPanel(); // Make sure this is the only instance created
            f.setContentPane((Container) categoryView);
            f.pack();
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            return f;
        });

        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @Test
    void testWhenNameAndDescriptionAreFilledThenAddButtonShouldBeEnabled() {
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture descriptionField = window.textBox("descriptionField");
        nameField.setText("Name");
        descriptionField.setText("Description");
        window.button(JButtonMatcher.withName("addButton")).requireEnabled();
    }


    @Test
    void testWhenEitherNameOrDescriptionAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture descriptionField = window.textBox("descriptionField");
        nameField.setText("Name");
        descriptionField.setText(" ");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        nameField.setText("");
        descriptionField.setText("");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        nameField.setText(" ");
        descriptionField.setText("Description");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
    }

    @Test
    void testStatusUpdateAfterAddingCategory() {
        execute(() -> categoryView.setStatus("Category added successfully"));
        JLabelFixture statusLabel = window.label("statusLabel");
        statusLabel.requireText("Category added successfully");
    }

    @Test
    void testShownCategoryShouldMatchCategoryAdded() {
        Category category = new Category("Name", "Description");
        category.setId(1L);
        execute(() -> categoryView.addCategoryToTable(category));
        DefaultTableModel model = (DefaultTableModel) categoryView.getCategoryTable().getModel();
        assertEquals(1, model.getRowCount(), "Table should have one row after adding a category");
        assertEquals(category.getId(), model.getValueAt(0, 0), "Category Id in the table should match the added category");
        assertEquals(category.getName(), model.getValueAt(0, 1), "Category name in the table should match the added category");
        assertEquals(category.getDescription(), model.getValueAt(0, 2), "Category Description in the table should match the added category");
    }

    @Test
    void testDeleteButtonShouldBeEnabledOnlyWhenACategoryIsSelected() {
        assertEquals(-1, window.table("entityTable").target().getSelectedRow());
        window.button("deleteButton").requireDisabled();

        testShownCategoryShouldMatchCategoryAdded();
        // Select the first row and assert that the delete button is enabled
        execute(() -> categoryView.getCategoryTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();

        // Clear selection and assert that the delete button is disabled
        execute(() -> categoryView.getCategoryTable().clearSelection());
        window.button("deleteButton").requireDisabled();
    }

    @Test
    void testFieldContentIsMatching() {
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture descriptionField = window.textBox("descriptionField");
        nameField.setText("Name");
        descriptionField.setText("Description");
        nameField.requireText(categoryView.getName());
        descriptionField.requireText(categoryView.getDescription());
        categoryView.setName("New Name");
        categoryView.setDescription("New Description");
        nameField.requireText(categoryView.getName());
        descriptionField.requireText(categoryView.getDescription());
    }

    @Test
    void testDeleteButtonShouldRemoveCategoryFromTable() {
        testShownCategoryShouldMatchCategoryAdded();
        JTableFixture tableFixture = window.table("entityTable");
        tableFixture.requireRowCount(1);
        // Select the first row and assert that the delete button is enabled
        execute(() -> categoryView.getCategoryTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();
        assertEquals(1L, categoryView.getCategoryIdFromTable(0));
        execute(() -> categoryView.removeCategoryFromTable(0));
        tableFixture.requireRowCount(0);
    }

    @Test
    void testClearFormShouldClearTextFields() {
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture descriptionField = window.textBox("descriptionField");

        nameField.setText("Name");
        descriptionField.setText("Description");
        execute(() -> categoryView.clearForm());

        nameField.requireText("");
        descriptionField.requireText("");
    }
}
