package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
}