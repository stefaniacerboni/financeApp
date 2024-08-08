package it.unifi.financeapp.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategorySteps {


    @Given("I am on the Category Management page")
    public void iAmOnTheCategoryManagementPage() {
        TestConfig.window.tabbedPane().selectTab(0);
    }

    @And("the category {string} exists")
    public void theCategoryExists(String categoryName) {
        TestConfig.window.table("entityTable").requireRowCount(1);
        assertEquals(categoryName, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 1));

    }

    @When("I select the {string} category")
    public void iSelectTheCategory(String categoryName) {
        assertEquals(categoryName, TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 1));
        TestConfig.window.table("entityTable").target().setRowSelectionInterval(0, 0);
    }

    @Then("I should not see any category in the category list")
    public void iShouldNotSeeInTheCategoryList() {
        TestConfig.window.table("entityTable").requireRowCount(0);
    }
}

