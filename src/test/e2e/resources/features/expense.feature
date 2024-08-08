Feature: Manage Expenses
  As a user, I want to add and manage expenses in an organized way, assigning them to a category and to a user.

  Scenario: Add a new expense
    Given The database contains a category and a user
    And I am on the Expense Management page
    When I select the first user
    And I select the first category
    And I enter "100" into the "amountField" field
    And I enter "2024-09-05" into the "dateField" field
    And I click the "addButton" button
    Then I should see "john.doe" in the list
    And I should see the amount of the expense equals to "100.0"

  Scenario: Delete a expense
    Given I am on the Expense Management page
    And the expense "100.0" exists
    When I select the "100.0" expense
    And I click the "deleteButton" button
    Then I should not see any expense in the expense list
