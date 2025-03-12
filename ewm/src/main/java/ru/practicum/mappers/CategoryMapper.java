package ru.practicum.mappers;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.models.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category model) {
        return new CategoryDto(
                model.getId(),
                model.getName()
        );
    }

    public static Category toModel(CategoryDto dto) {
        return new Category(
                dto.getId(),
                dto.getName()
        );
    }

    public static Category toModel(NewCategoryDto dto) {
        Category model = new Category();
        model.setName(dto.getName());
        return model;
    }
}
