package ru.practicum.repositories;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.Compilation;

import java.util.Collection;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    boolean existsByTitleAndIdNot(String name, Long id);

    Collection<Compilation> findByPinned(Boolean pinned, PageRequest pageRequest);
}
