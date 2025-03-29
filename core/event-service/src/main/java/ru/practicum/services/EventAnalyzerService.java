package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.AnalyzerClient;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.models.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


@Service
@AllArgsConstructor
public class EventAnalyzerService {
    private final AnalyzerClient client;

    public Double getRating(Event event) {
        return client.getInteractionsCount(List.of(event.getId()))
                .findFirst()
                .map(RecommendationsMessages.RecommendedEventProto::getScore)
                .orElse(0.0);
    }

    public HashMap<Long, Double> getRating(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<Long, Double> ratings = getSkeleton(events);
        if (events.isEmpty()) {
            return ratings;
        }
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        return client.getInteractionsCount(eventIds).collect(
                HashMap::new,
                (map, recommended) -> map.put(recommended.getEventId(), recommended.getScore()),
                HashMap::putAll
        );
    }

    public List<EventRecommendationDto> recommendations(Long userId, Integer limit) {
        var recommendations = client.getRecommendationsForUser(userId, limit).toList();
        List<EventRecommendationDto> result = new ArrayList<>();
        for (RecommendationsMessages.RecommendedEventProto rp : recommendations) {
            result.add(new EventRecommendationDto(rp.getEventId(), rp.getScore()));
        }
        return result;
    }

    private HashMap<Long, Double> getSkeleton(Collection<Event> events) {
        return events.stream().collect(
                HashMap::new,
                (map, event) -> map.put(
                        event.getId(),
                        0.0
                ),
                HashMap::putAll
        );
    }
}
