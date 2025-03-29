package ru.practicum.services.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;
import ru.practicum.repositories.CompilationRepository;
import ru.practicum.repositories.EventRepository;
import ru.practicum.services.EventAnalyzerService;
import ru.practicum.services.UserEventService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UserEventService userEventService;
    private final EventAnalyzerService eventAnalyzerService;

    public CompilationDto find(Long compilationId) {
        Optional<Compilation> optional = compilationRepository.findById(compilationId);
        if (optional.isEmpty()) {
            throw new NotFoundException("Compilation not found");
        }
        return getCompilation(optional.get());
    }

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Collection<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toModel(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Compilation saved: {}", compilation);
        return find(compilation.getId());
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        find(compilationId);
        compilationRepository.deleteById(compilationId);
        log.info("Compilation deleted, ID : {}", compilationId);
    }

    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long compilationId) {
        Collection<Event> events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
        if (updateCompilationRequest.getEvents() != null && updateCompilationRequest.getEvents().size() != events.size()) {
            throw new InvalidDataException("One or more events not found");
        }
        Compilation currentCompilation = compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException("Compilation not found"));
        Compilation compilation = CompilationMapper.mergeModel(currentCompilation, updateCompilationRequest, events);
        if (compilationRepository.existsByTitleAndIdNot(compilation.getTitle(), compilationId)) {
            throw new InvalidDataException("Compilation name already exists");
        }
        compilation = compilationRepository.save(compilation);
        log.info("Compilation updated, ID : {}", compilationId);
        return getCompilation(compilation);
    }

    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Collection<Compilation> compilations = pinned == null
                ? compilationRepository.findAll(PageRequest.of(from / size, size)).stream().toList()
                : compilationRepository.findByPinned(pinned, PageRequest.of(from / size, size));
        return compilations.stream().map(this::getCompilation).collect(Collectors.toList());
    }

    private CompilationDto getCompilation(Compilation compilation) {
        Collection<Event> events = compilation.getEvents();
        return CompilationMapper.toDto(compilation, userEventService.getUsersByEvents(events), eventAnalyzerService.getRating(events));
    }
}
