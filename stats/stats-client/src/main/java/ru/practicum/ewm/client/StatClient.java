package ru.practicum.ewm.client;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatDto;

import java.util.List;

@Component
@Slf4j
public class StatClient {
    private final RestClient restClient;

    StatClient(@Value("${stats-server.url}") String serverUrl) {
        restClient = RestClient.create(serverUrl);
    }

    public void hit(@Valid HitDto hitDto) {
        try {
            restClient.post().uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(hitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception exception) {
            log.error("При обращении к сервису статистики возникла ошибка: " + exception.getMessage());
        }
    }

    public List<StatDto> getStats(String start,
                                  String end,
                                  List<String> uris,
                                  Boolean unique) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve().body(new ParameterizedTypeReference<>() {
                    });
        } catch (ResourceAccessException ignored) {
        }
        return List.of();
    }
}
