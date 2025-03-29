package ru.practicum.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.services.UserActionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionsConsumer {

    private final UserActionService userActionService;

    @KafkaListener(
            topics = "${kafka.user-actions-consumer.topic}",
            containerFactory = "userActionsKafkaListenerFactory"
    )
    public void consumeUserActions(UserActionAvro message) {
        log.info("Consume User Action: {}", message);
        userActionService.updateUserAction(message);
    }
}
