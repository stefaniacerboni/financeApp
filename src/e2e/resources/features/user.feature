Feature: Manage Users
	As a user, I want to add and manage users so that I can organize expenses appropriately.

	Scenario: Add a new user
		Given I am on the User Management page
		When I enter "Username" into the "Username" field
		And I enter "Name" into the "Name" field
		And I enter "Surname" into the "Surname" field
		And I enter "Email" into the "Email" field
		And I click the "Add User" button
		Then I should see "Username" in the list

	Scenario: Delete a user
		Given I am on the User Management page
		And the user "Username" exists
		When I select the "Username" user
		And I click the "Delete User" button
		Then I should not see any user in the user list