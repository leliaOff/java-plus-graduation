package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.EventRequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_requests")
public class EventRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventRequestStatus status = EventRequestStatus.PENDING;
}
