Feature: Manage Users

  Scenario: Add a new user
    Given I am on the User Management page
    When I enter "Username" into the "usernameField" field
    And I enter "Email" into the "emailField" field
    And I click the "Add User" button
    Then I should see "Username" in the list

  Scenario: Delete a user
    Given I am on the User Management page
    And the user "Username" exists
    When I select the "Username" user
    And I click the "Delete User" button
    Then I should not see any user in the user list