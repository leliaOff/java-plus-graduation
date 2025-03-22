package ru.practicum.mappers;

import lombok.AllArgsConstructor;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.models.Compilation;
import ru.practicum.models.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CompilationMapper {
    public static CompilationDto toDto(Compilation model, Map<Long, UserDto> users, HashMap<Long, Long> views) {
        Collection<EventShortDto> events = new ArrayList<>();
        if (model.getEvents() != null) {
            events = model.getEvents().stream()
                    .map(event -> EventMapper.toShortDto(event, users.get(event.getInitiatorId()), views.get(event.getId())))
                    .collect(Collectors.toList());
        }
        return new CompilationDto(
                model.getId(),
                model.getTitle(),
                model.getPinned(),
                events
        );
    }

    public static Compilation toModel(NewCompilationDto dto, Collection<Event> events) {
        Compilation model = new Compilation();
        model.setTitle(dto.getTitle());
        model.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        if (!events.isEmpty()) {
            model.setEvents(events);
        }
        return model;
    }

    public static Compilation mergeModel(Compilation model, UpdateCompilationRequest dto, Collection<Event> events) {
        if (dto.getTitle() != null) {
            model.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            model.setPinned(dto.getPinned());
        }
        if (!events.isEmpty()) {
            model.setEvents(events);
        }
        return model;
    }
}
