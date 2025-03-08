package it.unifi.financeapp.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
		execute(() -> TestConfig.window.table("entityTable").target().setRowSelectionInterval(0, 0));
	}

	@Then("I should not see any category in the category list")
	public void iShouldNotSeeInTheCategoryList() {
		TestConfig.window.table("entityTable").requireRowCount(0);
	}

	@Then("I should see in the list")
	public void iShouldSeeInTheList(DataTable dataTable) {
		// Expected data table rows (each row: [Name, Description])
		List<List<String>> expectedRows = dataTable.asLists(String.class);

		// Retrieve the actual table contents
		String[][] actualArray = TestConfig.window.table("entityTable").contents();
		List<List<String>> actualRows = Arrays.stream(actualArray).map(Arrays::asList).collect(Collectors.toList());

		// Remove the first column (ID) from each row
		List<List<String>> actualRowsWithoutId = actualRows.stream().map(row -> row.subList(1, row.size()))
				.collect(Collectors.toList());

		assertThat(actualRowsWithoutId).containsExactlyElementsOf(expectedRows);

	}

	@Then("^I should see \"([^\"]*)\" in the status label$")
	public void iShouldSeeInTheStatusLabel(String statusText) {
		TestConfig.window.label("statusLabel").requireText(statusText);
	}
}
