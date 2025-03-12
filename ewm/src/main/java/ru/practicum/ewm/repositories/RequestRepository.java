package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.models.EventRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<EventRequest, Integer> {
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<EventRequest> findAllByEventId(Long eventId);

    List<EventRequest> findAllByRequesterId(Long userId);

    Optional<EventRequest> findByRequesterIdAndId(Long userId, Long requestId);

    @Query("SELECT er FROM EventRequest er WHERE er.event.id = :eventId AND er.event.initiator.id = :userId")
    List<EventRequest> findAllByEventIdWithInitiatorId(Long eventId, Long userId);
}
