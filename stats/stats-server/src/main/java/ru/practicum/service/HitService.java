package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.model.Hit;
import ru.practicum.model.HitMapper;
import ru.practicum.repository.HitRepository;

@Service
@Slf4j
public class HitService {
    private final HitRepository repository;

    @Autowired
    HitService(HitRepository repository) {
        this.repository = repository;
    }

    public HitDto create(HitDto dto) {
        Hit hit = HitMapper.toHit(dto);
        hit = repository.save(hit);
        return HitMapper.toHitDto(hit);
    }
}
