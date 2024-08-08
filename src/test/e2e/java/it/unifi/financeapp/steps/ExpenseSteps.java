package it.unifi.financeapp.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseSteps {

    @Given("The database contains a category and a user")
    public void theDatabaseContainsACategoryAndAUser() {
        TestConfig.prepareTestData();
    }

    @And("I am on the Expense Management page")
    public void iAmOnTheExpenseManagementPage() {
        TestConfig.window.tabbedPane().target().setSelectedIndex(2);
    }

    @When("I select the first user")
    public void iSelectTheFirstUser() {
        TestConfig.window.comboBox("userComboBox").selectItem(0);
    }

    @And("I select the first category")
    public void iSelectTheFirstCategory() {
        TestConfig.window.comboBox("categoryComboBox").selectItem(0);
    }

    @And("the expense {string} exists")
    public void theExpenseExists(String amount) {
        TestConfig.window.table("entityTable").requireRowCount(1);
        assertEquals(amount, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 3).toString());
    }

    @When("I select the {string} expense")
    public void iSelectTheExpense(String amount) {
        assertEquals(amount, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 3).toString());
        TestConfig.window.table("entityTable").target().setRowSelectionInterval(0, 0);

    }

    @Then("I should not see any expense in the expense list")
    public void iShouldNotSeeAnyExpenseInTheExpenseList() {
        TestConfig.window.table("entityTable").requireRowCount(0);
    }

    @And("I should see the amount of the expense equals to {string}")
    public void iShouldSeeTheAmountOfTheExpenseEqualsTo(String amount) {
        assertEquals(amount, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 3).toString());

    }
}
