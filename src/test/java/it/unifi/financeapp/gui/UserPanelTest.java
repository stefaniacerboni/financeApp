package it.unifi.financeapp.gui;


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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserPanelTest {

    private FrameFixture window;

    private UserView userView;

    @BeforeEach
    void setUp() {
        JFrame frame = GuiActionRunner.execute(() -> {
            JFrame f = new JFrame();
            userView = new UserPanel(); // Make sure this is the only instance created
            f.setContentPane((Container) userView);
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
    void testFieldContentIsMatching(){
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture nameField = window.textBox("nameField");
        JTextComponentFixture surnameField = window.textBox("surnameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        nameField.setText("Name");
        surnameField.setText("Surname");
        emailField.setText("Email");
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
    }

    @Test
    void testWhenUsernameAndEmailAreFilledThenAddButtonShouldBeEnabled() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        emailField.setText("Email");
        window.button(JButtonMatcher.withName("addUserButton")).requireEnabled();
    }


    @Test
    void testWhenEitherUsernameOrEmailAreBlankThenAddButtonShouldBeDisabled() {
        JTextComponentFixture usernameField = window.textBox("usernameField");
        JTextComponentFixture emailField = window.textBox("emailField");
        usernameField.setText("Username");
        emailField.setText(" ");
        window.button(JButtonMatcher.withName("addUserButton")).requireDisabled();
        usernameField.setText("");
        emailField.setText("");
        window.button(JButtonMatcher.withName("addUserButton")).requireDisabled();
        usernameField.setText(" ");
        emailField.setText("Email");
        window.button(JButtonMatcher.withName("addUserButton")).requireDisabled();
    }

    @Test
    void testStatusUpdateAfterAddingUser() {
        execute(() -> userView.setStatus("User added successfully"));
        JLabelFixture statusLabel = window.label("statusLabel");
        statusLabel.requireText("User added successfully");
    }

    @Test
    public void testShownUserShouldMatchUserAdded() {
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
    public void testDeleteButtonShouldBeEnabledOnlyWhenAUserIsSelected() {
        testShownUserShouldMatchUserAdded();
        // Select the first row and assert that the delete button is enabled
        execute(() -> userView.getUserTable().setRowSelectionInterval(0, 0));
        window.button("deleteUserButton").requireEnabled();

        // Clear selection and assert that the delete button is disabled
        execute(() -> userView.getUserTable().clearSelection());
        window.button("deleteUserButton").requireDisabled();
    }

    @Test
    public void testDeleteButtonShouldRemoveUserFromTable() {
        testShownUserShouldMatchUserAdded();
        JTableFixture tableFixture = window.table("userTable");
        tableFixture.requireRowCount(1);
        // Select the first row and assert that the delete button is enabled
        execute(() -> userView.getUserTable().setRowSelectionInterval(0, 0));
        assertEquals(1L, userView.getUserIdFromTable(0));
        window.button("deleteUserButton").requireEnabled();
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
}
