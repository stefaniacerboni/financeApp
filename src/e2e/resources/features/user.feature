Feature: Manage Users
	As a user, I want to add and manage users so that I can organize expenses appropriately.
	
	Scenario: The initial state of the view
		Given The database contains the users with the following values
			| Username1 | Name1 | Surname1 | Email1 |
			| Username2 | Name2 | Surname2 | Email2 |
		When I am on the User Management page
		Then I should see in the list
			| Username1 | Name1 | Surname1 | Email1 |
			| Username2 | Name2 | Surname2 | Email2 |
		
	Scenario: Add a new user
		Given I am on the User Management page
		When I enter "Username" into the "Username" field
		And I enter "Name" into the "Name" field
		And I enter "Surname" into the "Surname" field
		And I enter "Email" into the "Email" field
		And I click the "Add User" button
		Then I should see "Username" in the list

	Scenario: Delete a user
		Given The database contains the users with the following values
			| Username | Name | Surname | Email |
		And I am on the User Management page
		When I select the "Username" user
		And I click the "Delete User" button
		Then I should not see any user in the user list
		
	Scenario: Add an existing user should rise error
		Given The database contains the users with the following values
			| Username1 | Name1 | Surname1 | Email1 |
			| Username2 | Name2 | Surname2 | Email2 |
		When I am on the User Management page
		And I enter "Username1" into the "Username" field
		And I enter "Email1" into the "Email" field
		And I click the "Add User" button
		Then I should see "Failed to add user: Persistence error." in the status label
		
	Scenario: Delete a user with dependencies should rise error
		Given The database contains a category or user connected to an expense
		And I am on the User Management page
		When I select the "john.doe" user
		And I click the "Delete User" button
		Then I should see "Cannot delete user with existing expenses" in the status label