package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.StatDto;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.model.StatMapper;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatService {
    private final HitRepository repository;

    @Autowired
    StatService(HitRepository repository) {
        this.repository = repository;
    }

    public Collection<StatDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new BadRequestException("end must be after start");
        }
        if (unique) {
            if (uris == null) {
                return repository.getUniqueStat(start, end).stream().map(StatMapper::toDto).collect(Collectors.toList());
            }
            return repository.getUniqueStat(start, end, uris).stream().map(StatMapper::toDto).collect(Collectors.toList());
        }
        if (uris == null) {
            return repository.getStat(start, end).stream().map(StatMapper::toDto).collect(Collectors.toList());
        }
        List<StatDto> stats = repository.getStat(start, end, uris).stream().map(StatMapper::toDto).collect(Collectors.toList());
        return repository.getStat(start, end, uris).stream().map(StatMapper::toDto).collect(Collectors.toList());
    }
}
