package it.unifi.financeapp.controller;

import it.unifi.financeapp.gui.CategoryView;
import it.unifi.financeapp.model.Category;
import it.unifi.financeapp.service.CategoryService;
import it.unifi.financeapp.service.exceptions.InvalidCategoryException;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
	@Mock
	private CategoryService categoryService;
	@Mock
	private CategoryView categoryView;

	@InjectMocks
	private CategoryController controller;

	@BeforeEach
	void setUp() {
		controller = new CategoryController(categoryService, categoryView);
		controller.initView();
	}

	@Nested
	@DisplayName("Happy Cases")
	class HappyCases {

		@Test
		void shouldInitializeView() {
			verify(categoryService).getAllCategories(); // loadCategories() is called in initView()
		}

		@Test
		void testLoadCategoriesOnInit() {
			List<Category> mockCategories = Arrays.asList(new Category("1", "Food"), new Category("2", "Utilities"));
			when(categoryService.getAllCategories()).thenReturn(mockCategories);

			controller.initView();

			verify(categoryView, times(mockCategories.size())).addCategoryToTable(any(Category.class));
		}

		@Test
		void testAddCategorySuccessfully() {
			when(categoryView.getName()).thenReturn("New Category");
			when(categoryView.getDescription()).thenReturn("New Description");
			Category newCategory = new Category("New Category", "New Description");
			when(categoryService.addCategory(any(Category.class))).thenReturn(newCategory);

			controller.addCategory();

			verify(categoryService).addCategory(newCategory);
			verify(categoryView).addCategoryToTable(newCategory);
			verify(categoryView).setStatus("Category added successfully.");
			verify(categoryView).clearForm();
		}

		@Test
		void testNewCategoryConcurrent() {
			List<Category> categories = new ArrayList<>();
			Category category = new Category("Name", "Description");
			doAnswer(invocation -> {
				categories.add(category);
				return null;
			}).when(categoryService).addCategory(any(Category.class));
			when(categoryView.getName()).thenReturn("Name");
			when(categoryView.getDescription()).thenReturn("Description");
			List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> controller.addCategory()))
					.peek(Thread::start).collect(Collectors.toList());
			await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		}

		@Test
		void testDeleteSelectedCategory() {
			when(categoryView.getSelectedCategoryIndex()).thenReturn(0);
			when(categoryView.getCategoryIdFromTable(0)).thenReturn(1L);

			controller.deleteCategory();

			verify(categoryService).deleteCategory(1L);
			verify(categoryView).removeCategoryFromTable(0);
			verify(categoryView).setStatus("Category deleted successfully.");
		}
	}

	@Nested
	@DisplayName("Bad Cases")
	class BadCases {
		
		@Test
		void testDuplicatedCategory() {
			when(categoryView.getName()).thenReturn("Category Name");
			when(categoryView.getDescription()).thenReturn("Category Description");
			when(categoryService.addCategory(any(Category.class))).thenThrow(ServiceException.class);

			controller.addCategory();

			verify(categoryView).setStatus("Failed to add category: Persistence error.");
		}

		@Test
		void testNotDeleteIfNoCategorySelected() {
			when(categoryView.getSelectedCategoryIndex()).thenReturn(-1);

			controller.deleteCategory();

			verify(categoryView, never()).getCategoryIdFromTable(anyInt());
			verify(categoryService, never()).deleteCategory(anyLong());
			verify(categoryView).setStatus("No category selected for deletion.");
		}

		@Test
		void testAddCategoryFailure() {
			when(categoryView.getName()).thenReturn("New Category");
			when(categoryView.getDescription()).thenReturn("New Description");
			when(categoryService.addCategory(any(Category.class))).thenReturn(null);

			controller.addCategory();

			verify(categoryView).setStatus("Failed to add category.");
		}

		@Test
		void testNotDeleteIfCategoryHasDependencies() {
			Long id = 1L;
			when(categoryView.getSelectedCategoryIndex()).thenReturn(0);
			when(categoryView.getCategoryIdFromTable(0)).thenReturn(id);
			doThrow(new InvalidCategoryException("Cannot delete category with existing expenses")).when(categoryService)
					.deleteCategory(id);

			controller.deleteCategory();

			verify(categoryView).setStatus("Cannot delete category with existing expenses");
		}
	}

}