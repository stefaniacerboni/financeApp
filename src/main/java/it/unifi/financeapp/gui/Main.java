package it.unifi.financeapp.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

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
		// Use try-with-resources so that the EntityManagerFactory is closed when done.
		try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("FinanceAppPU")) {
			EntityManager em = emf.createEntityManager();
			CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
			CategoryService categoryService = new CategoryService(categoryRepository);
			UserRepository userRepository = new UserRepositoryImpl(em);
			UserService userService = new UserService(userRepository);
			ExpenseRepository expenseRepository = new ExpenseRepositoryImpl(em);
			ExpenseService expenseService = new ExpenseService(expenseRepository);

			// Prepare the main frame and use a latch to wait until it is closed.
			MainFrame mf = new MainFrame(categoryService, userService, expenseService);
			mf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			CountDownLatch latch = new CountDownLatch(1);
			mf.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					latch.countDown();
				}
			});

			mf.setVisible(true);

			// Wait until the main frame is closed before exiting the try block.
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}