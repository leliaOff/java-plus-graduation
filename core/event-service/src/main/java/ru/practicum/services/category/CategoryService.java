package ru.practicum.services.category;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long categoryId);
}
