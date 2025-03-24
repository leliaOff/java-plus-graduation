package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.models.EventRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<EventRequest, Integer> {
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<EventRequest> findAllByEventId(Long eventId);

    List<EventRequest> findAllByRequesterId(Long userId);

    Optional<EventRequest> findByRequesterIdAndId(Long userId, Long requestId);
}
