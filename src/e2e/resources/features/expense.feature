Feature: Manage Expenses
	As a user, I want to add and manage expenses in an organized way, assigning them to a category and to a user.
	
	Scenario: The initial state of the view
		Given The database contains a category and a user
		And The database contains the expenses with the following values
			| john.doe | Utilities | 200.0 | 2024-09-05 |
			| john.doe | Utilities | 50.0 | 2024-09-04 |
		When I am on the Expense Management page
		Then I should see in the list
			| john.doe | Utilities | 200.0 | 2024-09-05 |
			| john.doe | Utilities | 50.0 | 2024-09-04 |

	Scenario: Add a new expense
		Given The database contains a category and a user
		And I am on the Expense Management page
		When I select the first user
		And I select the first category
		And I enter "100" into the "Amount" field
		And I enter "2024-09-05" into the "Date" field
		And I click the "Add Expense" button
		Then I should see "john.doe" in the list
		And I should see the amount of the expense equals to "100.0"

	Scenario: Delete an expense
		Given The database contains the expenses with the following values
			| john.doe | Utilities | 100.0 | 2024-09-05 |
		And I am on the Expense Management page
		And the expense "100.0" exists
		When I select the "100.0" expense
		And I click the "Delete Expense" button
		Then I should not see any expense in the expense list
		
	Scenario: Adding an expense with invalid date format
		Given The database contains a category and a user
		And I am on the Expense Management page
		When I select the first user
		And I select the first category
		And I enter "100" into the "Amount" field
		And I enter "5/03/2024" into the "Date" field
		And I click the "Add Expense" button
		Then I should see "Failed to add expense: Date is invalid." in the status label