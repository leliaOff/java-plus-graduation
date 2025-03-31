package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.models.UserAction;

import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    UserAction findByUserIdAndEventId(Long userId, Long eventId);

    List<UserAction> findByUserId(Long userId);

    List<UserAction> findByEventId(Long eventId);
}