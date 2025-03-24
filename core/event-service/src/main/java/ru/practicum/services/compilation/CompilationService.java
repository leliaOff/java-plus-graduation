package ru.practicum.services.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {
    CompilationDto find(Long compilationId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compilationId);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compilationId);

    Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
