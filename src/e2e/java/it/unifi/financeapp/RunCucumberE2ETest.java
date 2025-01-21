package it.unifi.financeapp;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/e2e/resources/features",
        glue = "it.unifi.financeapp.steps",
        plugin = {"pretty", "html:target/cucumber-report.html"}
)
public class RunCucumberE2ETest {
}
