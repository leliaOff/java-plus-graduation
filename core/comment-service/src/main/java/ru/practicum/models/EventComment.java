package ru.practicum.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.EventCommentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_comments")
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private Long authorId;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventCommentStatus status = EventCommentStatus.PENDING;
}
