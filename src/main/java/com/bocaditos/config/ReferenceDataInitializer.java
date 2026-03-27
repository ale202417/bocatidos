package com.bocaditos.config;

import com.bocaditos.domain.expense.ExpenseCategory;
import com.bocaditos.repository.ExpenseCategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReferenceDataInitializer {

    @Bean
    CommandLineRunner initializeReferenceData(ExpenseCategoryRepository expenseCategoryRepository) {
        return args -> {
            List<String> defaultCategories = List.of("Ingredients", "Packaging", "Transport", "Utilities");
            for (String categoryName : defaultCategories) {
                expenseCategoryRepository.findByNameIgnoreCase(categoryName)
                        .orElseGet(() -> {
                            ExpenseCategory category = new ExpenseCategory();
                            category.setName(categoryName);
                            category.setDescription("Default expense category");
                            return expenseCategoryRepository.save(category);
                        });
            }
        };
    }
}
