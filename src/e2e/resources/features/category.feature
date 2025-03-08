Feature: Manage Categories
	As a user, I want to add and manage categories so that I can organize expenses appropriately.

	Scenario: The initial state of the view
		Given The database contains the categories with the following values
			| Coffee | Category about coffee |
			| Travel | Category about travel |
		And I am on the User Management page
		When I am on the Category Management page		
		Then I should see in the list
			| Coffee | Category about coffee |
			| Travel | Category about travel |
			
	Scenario: Add a new category
		Given I am on the User Management page
		When I am on the Category Management page
		And I enter "Travel" into the "Name" field
		And I enter "Expenses for travel" into the "Description" field
		And I click the "Add Category" button
		Then I should see "Travel" in the list

	Scenario: Delete a category
		Given The database contains the categories with the following values
			| Travel | Category about travel |		
		And I am on the User Management page
		When I am on the Category Management page		
		And I select the "Travel" category
		And I click the "Delete Category" button
		Then I should not see any category in the category list
		
	Scenario: Add an existing category
		Given I am on the User Management page
		And The database contains the categories with the following values
			| Coffee | Category about coffee |
			| Travel | Category about travel |
		When I am on the Category Management page
		And I enter "Coffee" into the "Name" field
		And I enter "Category about coffee" into the "Description" field
		And I click the "Add Category" button
		Then I should see "Failed to add category: Persistence error." in the status label

	Scenario: Delete a category with dependencies should rise error
		Given The database contains a category or user connected to an expense
		And I am on the User Management page
		When I am on the Category Management page
		And I select the "Utilities" category		
		And I click the "Delete Category" button
		Then I should see "Cannot delete category with existing expenses" in the status label