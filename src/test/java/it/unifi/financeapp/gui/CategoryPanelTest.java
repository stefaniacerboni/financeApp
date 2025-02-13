package it.unifi.financeapp.gui;

import it.unifi.financeapp.controller.CategoryController;
import it.unifi.financeapp.model.Category;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JLabelFixture;
import org.assertj.swing.fixture.JTableFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(GUITestExtension.class)
@ExtendWith(MockitoExtension.class)
class CategoryPanelTest {
	@Mock
	private CategoryController categoryController;

	private FrameFixture window;

	private CategoryPanel categoryView;

	@BeforeEach
	void setUp() {
		JFrame frame = GuiActionRunner.execute(() -> {
			JFrame f = new JFrame();
			categoryView = new CategoryPanel();
			categoryView.setCategoryController(categoryController);
			f.setContentPane(categoryView);
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

	@Nested
	@DisplayName("Happy Cases")
	class HappyCases {
		
		@Test
		@GUITest
		void testFieldContentIsMatching() {
			JTextComponentFixture nameField = window.textBox("Name");
			JTextComponentFixture descriptionField = window.textBox("Description");
			assertFalse(categoryView.getAddCategoryButton().isEnabled());
			nameField.setText("Name");
			descriptionField.setText("Description");
			nameField.requireText(categoryView.getName());
			descriptionField.requireText(categoryView.getDescription());
			assertTrue(categoryView.getAddCategoryButton().isEnabled());
			categoryView.setName("New Name");
			categoryView.setDescription("New Description");
			nameField.requireText(categoryView.getName());
			descriptionField.requireText(categoryView.getDescription());
			JTableFixture entityTable = window.table("entityTable");
			entityTable.requireRowCount(0);
			assertEquals(-1, categoryView.getSelectedCategoryIndex());
		}

		@Test
		@GUITest
		void testWhenNameAndDescriptionAreFilledThenAddButtonShouldBeEnabled() {
			JTextComponentFixture nameField = window.textBox("Name");
			JTextComponentFixture descriptionField = window.textBox("Description");
			nameField.setText("Name");
			descriptionField.setText("Description");
			window.button(JButtonMatcher.withName("addButton")).requireEnabled();
		}

		@Test
		@GUITest
		void testStatusUpdateAfterAddingCategory() {
			execute(() -> categoryView.setStatus("Category added successfully"));
			JLabelFixture statusLabel = window.label("statusLabel");
			statusLabel.requireText("Category added successfully");
		}

		private Category addCategoryToTable() {
			Category category = new Category("Name", "Description");
			category.setId(1L);
			execute(() -> categoryView.addCategoryToTable(category));
			return category;
		}

		@Test
		@GUITest
		void testShownCategoryShouldMatchCategoryAdded() {
			Category category = addCategoryToTable();
			DefaultTableModel model = (DefaultTableModel) categoryView.getCategoryTable().getModel();
			assertEquals(1, model.getRowCount(), "Table should have one row after adding a category");
			assertEquals(category.getId(), model.getValueAt(0, 0),
					"Category Id in the table should match the added category");
			assertEquals(category.getName(), model.getValueAt(0, 1),
					"Category name in the table should match the added category");
			assertEquals(category.getDescription(), model.getValueAt(0, 2),
					"Category Description in the table should match the added category");
		}

		@Test
		@GUITest
		void testDeleteButtonShouldBeEnabledOnlyWhenACategoryIsSelected() {
			assertEquals(-1, window.table("entityTable").target().getSelectedRow());
			window.button("deleteButton").requireDisabled();

			addCategoryToTable();

			// Select the first row and assert that the delete button is enabled
			execute(() -> categoryView.getCategoryTable().setRowSelectionInterval(0, 0));
			window.button("deleteButton").requireEnabled();

			// Clear selection and assert that the delete button is disabled
			execute(() -> categoryView.getCategoryTable().clearSelection());
			window.button("deleteButton").requireDisabled();
		}

		@Test
		@GUITest
		void testDeleteButtonShouldRemoveCategoryFromTable() {
			addCategoryToTable();
			JTableFixture tableFixture = window.table("entityTable");
			tableFixture.requireRowCount(1);
			// Select the first row and assert that the delete button is enabled
			execute(() -> categoryView.getCategoryTable().setRowSelectionInterval(0, 0));
			window.button("deleteButton").requireEnabled();
			assertTrue(categoryView.getDeleteCategoryButton().isEnabled());
			assertEquals(1L, categoryView.getCategoryIdFromTable(0));
			execute(() -> categoryView.removeCategoryFromTable(0));
			tableFixture.requireRowCount(0);
		}

		@Test
		@GUITest
		void testClearFormShouldClearTextFields() {
			JTextComponentFixture nameField = window.textBox("Name");
			JTextComponentFixture descriptionField = window.textBox("Description");

			nameField.setText("Name");
			descriptionField.setText("Description");
			execute(() -> categoryView.clearForm());

			nameField.requireText("");
			descriptionField.requireText("");
		}

		@Test
		@GUITest
		void testAddCategoryShouldDelegateToCategoryController() {
			JTextComponentFixture nameField = window.textBox("Name");
			JTextComponentFixture descriptionField = window.textBox("Description");
			nameField.setText("Name");
			descriptionField.setText("Description");
			execute(() -> window.button(JButtonMatcher.withName("addButton")).target().doClick());
			verify(categoryController).addCategory();
		}

		@Test
		@GUITest
		void testDeleteCategoryShouldDelegateToCategoryController() {
			addCategoryToTable();
			execute(() -> categoryView.getCategoryTable().setRowSelectionInterval(0, 0));
			window.button("deleteButton").requireEnabled();
			execute(() -> window.button(JButtonMatcher.withName("deleteButton")).target().doClick());
			verify(categoryController).deleteCategory();
		}
	}

	@Nested
	@DisplayName("Bad Cases")
	class BadCases {

		@Test
		@GUITest
		void testWhenEitherNameOrDescriptionAreBlankThenAddButtonShouldBeDisabled() {
			JTextComponentFixture nameField = window.textBox("Name");
			JTextComponentFixture descriptionField = window.textBox("Description");
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
	}
}