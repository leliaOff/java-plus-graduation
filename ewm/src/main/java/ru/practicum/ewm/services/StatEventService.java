package ru.practicum.ewm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.models.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StatEventService {
    private final StatClient statClient;

    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String appName;

    public StatEventService(StatClient statClient, @Value("${app.name}") String appName) {
        this.statClient = statClient;
        this.appName = appName;
    }

    public void hit(String uri, String ip) {
        statClient.hit(new HitDto(this.appName, uri, ip, LocalDateTime.now()));
    }

    public Long getViews(Event event) {
        List<StatDto> stats = statClient.getStats(
                getStartDate(event),
                getEndDate(),
                getEventUris(event),
                true
        );
        if (stats.isEmpty()) {
            return 0L;
        }
        return stats.getFirst().getHits();
    }

    public HashMap<Long, Long> getViews(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<Long, Long> stats = getStatsSkeleton(events);
        HashMap<String, Long> urisMap = getUrisMap(events);
        if (events.isEmpty()) {
            return stats;
        }
        List<StatDto> statDtoList = statClient.getStats(
                getStartDate(events),
                getEndDate(),
                getEventUris(events),
                true
        );
        if (stats.isEmpty()) {
            return stats;
        }
        statDtoList.forEach(statDto -> {
            Long eventId = urisMap.get(statDto.getUri());
            stats.put(eventId, statDto.getHits());
        });
        return stats;
    }

    private String getEventUri(Event event) {
        return "/events/" + event.getId();
    }

    private List<String> getEventUris(Event event) {
        return List.of(getEventUri(event));
    }

    private List<String> getEventUris(Collection<Event> events) {
        return events.stream().map(this::getEventUri).toList();
    }

    private String getStartDate(Event event) {
        return event.getCreatedOn().format(pattern);
    }

    private String getStartDate(Collection<Event> events) {
        Optional<LocalDateTime> createdOn = events.stream().map(Event::getCreatedOn).min(LocalDateTime::compareTo);
        return createdOn.map(localDateTime -> localDateTime.format(pattern)).orElse(null);
    }

    private String getEndDate() {
        return LocalDateTime.now().format(pattern);
    }

    private HashMap<Long, Long> getStatsSkeleton(Collection<Event> events) {
        return events.stream().collect(
                HashMap::new,
                (map, event) -> map.put(
                        event.getId(),
                        0L
                ),
                HashMap::putAll
        );
    }

    private HashMap<String, Long> getUrisMap(Collection<Event> events) {
        return events.stream().collect(
                HashMap::new,
                (map, event) -> map.put(
                        getEventUri(event),
                        event.getId()
                ),
                HashMap::putAll
        );
    }
}
