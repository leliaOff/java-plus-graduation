package ru.practicum.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.dto.event.EventShortDto;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private Collection<EventShortDto> events;
}
