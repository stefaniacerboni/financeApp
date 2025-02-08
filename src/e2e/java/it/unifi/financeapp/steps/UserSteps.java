package it.unifi.financeapp.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserSteps {

    @Given("I am on the User Management page")
    public void iAmOnTheUserManagementPage() {
        TestConfig.window.tabbedPane().target().setSelectedIndex(1);
    }

    @And("the user {string} exists")
    public void theUserExists(String username) {
        TestConfig.window.table("entityTable").requireRowCount(1);
        assertEquals(username, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 1));
    }

    @When("I select the {string} user")
    public void iSelectTheUser(String username) {
        assertEquals(username, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 1));
        TestConfig.window.table("entityTable").target().setRowSelectionInterval(0, 0);
    }

    @Then("I should not see any user in the user list")
    public void iShouldNotSeeAnyUserInTheUserList() {
        TestConfig.window.table("entityTable").requireRowCount(0);
    }

}