package ru.practicum.ewm.services.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exceptions.InvalidDataException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.mappers.CategoryMapper;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.repositories.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new InvalidDataException("Category name already exists");
        }
        Category category = CategoryMapper.toModel(newCategoryDto);
        categoryRepository.save(category);
        log.info("Category saved: {}", category);
        return CategoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new InvalidDataException("Events in this category still exist");
        }

        categoryRepository.deleteById(categoryId);
        log.info("Category deleted, ID : {}", categoryId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        if (categoryRepository.existsByNameAndIdNot(categoryDto.getName(), categoryId)) {
            throw new InvalidDataException("Category name already exists");
        }
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return CategoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Category> categoriesPage = categoryRepository.findAll(pageable);

        if (categoriesPage.isEmpty()) {
            return new ArrayList<>();
        }

        List<CategoryDto> categoryDtos = categoriesPage
                .stream()
                .map(CategoryMapper::toDto)
                .toList();

        log.info("Receiving categories, total: {}, current page size: {}", categoriesPage.getTotalElements(), categoryDtos.size());

        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not found"));
        log.info("Receiving category, ID : {}", categoryId);
        return CategoryMapper.toDto(category);
    }
}
