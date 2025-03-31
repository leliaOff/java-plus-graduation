package ru.practicum.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.services.SimilarityService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionsConsumer {
    private final SimilarityService similarityService;

    @KafkaListener(
            topics = "#{kafkaProperties.consumer.topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserAction(UserActionAvro message) {
        log.info("Consume User Action: {}", message);
        similarityService.processUserAction(message);
    }
}
