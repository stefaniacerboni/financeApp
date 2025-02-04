package it.unifi.financeapp.gui;


import it.unifi.financeapp.controller.UserController;
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
class UserPanelTest {
    @Mock
    private UserController userController;

    private FrameFixture window;

    private UserPanel userView;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            userView = new UserPanel();
            userView.setUserController(userController);
            f.setContentPane(userView);
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
    void testFieldContentIsMatching() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture surnameField = window.textBox("surnameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        assertFalse(userView.getAddUserButton().isEnabled());
        usernameField.setText("Username");
        nameField.setText("Name");
        surnameField.setText("Surname");
        emailField.setText("Email");
        assertTrue(userView.getAddUserButton().isEnabled());
        usernameField.requireText(userView.getUsername());
        nameField.requireText(userView.getName());
        surnameField.requireText(userView.getSurname());
        emailField.requireText(userView.getEmail());
        userView.setUsername("New Username");
        userView.setName("New Name");
        userView.setSurname("New Surname");
        userView.setEmail("New Email");
        usernameField.requireText(userView.getUsername());
        nameField.requireText(userView.getName());
        surnameField.requireText(userView.getSurname());
        emailField.requireText(userView.getEmail());
        JTableFixture entityTable = window.table("entityTable");
        entityTable.requireRowCount(0);
        assertEquals(-1, userView.getSelectedUserIndex());

    }

    @Test
    void testWhenUsernameAndEmailAreFilledThenAddButtonShouldBeEnabled() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        emailField.setText("Email");
        window.button(JButtonMatcher.withName("addButton")).requireEnabled();
    }


    @Test
    void testWhenEitherUsernameOrEmailAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        emailField.setText(" ");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        usernameField.setText("");
        emailField.setText("");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
        usernameField.setText(" ");
        emailField.setText("Email");
        window.button(JButtonMatcher.withName("addButton")).requireDisabled();
    }

    @Test
    void testStatusUpdateAfterAddingUser() {
        execute(() -> userView.setStatus("User added successfully"));
        JLabelFixture statusLabel = window.label("statusLabel");
        statusLabel.requireText("User added successfully");
    }

    @Test
    void testShownUserShouldMatchUserAdded() {
        User user = new User("Username", "Email");
        user.setId(1L);
        execute(() -> userView.addUserToTable(user));
        DefaultTableModel model = (DefaultTableModel) userView.getUserTable().getModel();
        assertEquals(1, model.getRowCount(), "Table should have one row after adding a user");
        assertEquals(user.getId(), model.getValueAt(0, 0), "User Id in the table should match the added user");
        assertEquals(user.getUsername(), model.getValueAt(0, 1), "User Username in the table should match the added user");
        assertEquals(user.getEmail(), model.getValueAt(0, 4), "User Email in the table should match the added user");
    }

    @Test
    void testDeleteButtonShouldBeEnabledOnlyWhenAUserIsSelected() {
        testShownUserShouldMatchUserAdded();
        // Select the first row and assert that the delete button is enabled
        execute(() -> userView.getUserTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();
        assertTrue(userView.getDeleteUserButton().isEnabled());


        // Clear selection and assert that the delete button is disabled
        execute(() -> userView.getUserTable().clearSelection());
        window.button("deleteButton").requireDisabled();
    }

    @Test
    void testDeleteButtonShouldRemoveUserFromTable() {
        testShownUserShouldMatchUserAdded();
        JTableFixture tableFixture = window.table("entityTable");
        tableFixture.requireRowCount(1);
        // Select the first row and assert that the delete button is enabled
        execute(() -> userView.getUserTable().setRowSelectionInterval(0, 0));
        assertEquals(1L, userView.getUserIdFromTable(0));
        window.button("deleteButton").requireEnabled();
        execute(() -> userView.removeUserFromTable(0));
        tableFixture.requireRowCount(0);
    }

    @Test
    void testClearFormShouldClearTextFields() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");

        usernameField.setText("Username");
        emailField.setText("Email");
        execute(() -> userView.clearForm());

        usernameField.requireText("");
        emailField.requireText("");
    }


    @Test
    void testAddUserShouldDelegateToUserController() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        emailField.setText("Email");
        execute(() -> window.button(JButtonMatcher.withName("addButton")).target().doClick());
        verify(userController).addUser();
    }

    @Test
    void testDeleteUserShouldDelegateToUserController() {
        testShownUserShouldMatchUserAdded();
        execute(() -> userView.getUserTable().setRowSelectionInterval(0, 0));
        window.button("deleteButton").requireEnabled();
        execute(() -> window.button(JButtonMatcher.withName("deleteButton")).target().doClick());
        verify(userController).deleteUser();
    }
}
