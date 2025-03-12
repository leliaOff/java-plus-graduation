package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.models.Category;

import java.util.Collection;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    @Query("SELECT category FROM Category category WHERE category.id IN (:ids)")
    List<Category> findByIdIn(@Param("ids") Collection<Long> ids);
}
