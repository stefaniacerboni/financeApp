package it.unifi.financeapp.steps;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.swing.core.matcher.JButtonMatcher;

import static org.assertj.swing.edt.GuiActionRunner.execute;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonSteps {

    @BeforeAll
    public static void setUp() {
        TestConfig.setUpClass();
    }

    @AfterAll
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
}
