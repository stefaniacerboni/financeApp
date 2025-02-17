package it.unifi.financeapp.gui;

import it.unifi.financeapp.repository.CategoryRepository;
import it.unifi.financeapp.repository.CategoryRepositoryImpl;
import it.unifi.financeapp.repository.ExpenseRepository;
import it.unifi.financeapp.repository.ExpenseRepositoryImpl;
import it.unifi.financeapp.repository.UserRepository;
import it.unifi.financeapp.repository.UserRepositoryImpl;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.ExpenseService;
import it.unifi.financeapp.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("FinanceAppPU");

		// Register a shutdown hook to close the EntityManagerFactory on JVM exit
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (emf.isOpen()) {
				emf.close();
			}
		}));

		EntityManager em = emf.createEntityManager();
		CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
		CategoryService categoryService = new CategoryService(categoryRepository);
		UserRepository userRepository = new UserRepositoryImpl(em);
		UserService userService = new UserService(userRepository);
		ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
		ExpenseService expenseService = new ExpenseService(expenseRepository);
		MainFrame mf = new MainFrame(categoryService, userService, expenseService);
		mf.setVisible(true);
	}
}