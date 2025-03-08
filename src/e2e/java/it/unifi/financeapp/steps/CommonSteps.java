package it.unifi.financeapp.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.matcher.JButtonMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommonSteps {

	@Before
	public static void setUp() {
		TestConfig.setUpClass();
	}

	@After
	public static void onTearDown() {
		TestConfig.tearDownClass();
	}

	@When("I enter {string} into the {string} field")
	public void i_enter_into_the_field(String text, String fieldName) {
		TestConfig.window.textBox(fieldName).setText(text);
	}

	@And("I click the {string} button")
	public void i_click_the_button(String buttonText) {
		TestConfig.window.button(JButtonMatcher.withText(buttonText)).requireEnabled();
		execute(() -> TestConfig.window.button(JButtonMatcher.withText(buttonText)).target().doClick());
	}

	@Then("I should see {string} in the list")
	public void iShouldSeeInTheList(String value) {
		TestConfig.window.table("entityTable").requireRowCount(1);
		assertEquals(TestConfig.window.table("entityTable").target().getModel().getValueAt(0, 1), value);
	}

	@Given("^Clean the db first$")
	public void cleanTheDbFirst() {
		TestConfig.cleanUpDB();
	}
	
	@Then("I should see in the list")
	public void iShouldSeeInTheList(DataTable dataTable) {
		TestConfig.window.robot().waitForIdle();
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
}