package ru.practicum.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events_similarity")
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "event_a")
    private Long eventA;

    @Column(nullable = false, name = "event_b")
    private Long eventB;
    private Float score;
    private Instant timestamp;
}
