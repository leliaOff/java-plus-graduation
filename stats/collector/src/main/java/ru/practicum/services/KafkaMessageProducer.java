package ru.practicum.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.configurations.KafkaProperties;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {
    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;
    private final KafkaProperties properties;

    @Override
    public void sendUserAction(UserActionAvro userActionAvro) {
        kafkaTemplate.send(properties.getUserActionsTopic(), userActionAvro);
    }
}
