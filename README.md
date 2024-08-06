# FinanceApp

FinanceApp is a comprehensive financial management application designed using Test-Driven Development (TDD). This application supports tracking of expenses, categories, and user data.

## Features

- **User Management**: Add, edit, and delete users.
- **Category Management**: Organize expenses into categories.
- **Expense Tracking**: Record expenses under various categories and users.

## Technologies

- Java 11
- Maven for dependency management
- JUnit 5 for unit testing
- AssertJ for fluent assertion library
- Hibernate for ORM
- MySQL for database
- TestContainers for integration tests
- Swing for the user interface

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What you need to install the software:

- Java JDK 11
- Maven
- MySQL Server

### Installing

A step by step series of examples that tell you how to get a development environment running:

1. Clone the repository:
   ```bash
   git clone https://github.com/stefaniacerboni/financeApp.git```
  
2. Navigate to the project directory:
   ```bash
   cd financeApp```

3. Build the project with Maven:
   ```bash
   mvn clean install```

4. Run the application:
```bash
java -jar target/financeAppTDD-1.0-SNAPSHOT.jar
```

5. Running the tests
Explain how to run the automated tests for this system:

```bash
mvn test # for unit tests
mvn verify # for integration tests
```

## Authors
Stefania Cerboni 

## Code Coverage

[![Coverage Status](https://coveralls.io/repos/github/stefaniacerboni/financeApp/badge.svg)](https://coveralls.io/github/stefaniacerboni/financeApp)