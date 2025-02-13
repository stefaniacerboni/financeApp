Feature: Manage Categories
  As a user, I want to add and manage categories so that I can organize expenses appropriately.

  Scenario: Add a new category
    Given I am on the Category Management page
    When I enter "Travel" into the "Name" field
    And I enter "Expenses for travel" into the "Description" field
    And I click the "Add Category" button
    Then I should see "Travel" in the list

  Scenario: Delete a category
    Given I am on the Category Management page
    And the category "Travel" exists
    When I select the "Travel" category
    And I click the "Delete Category" button
    Then I should not see any category in the category list