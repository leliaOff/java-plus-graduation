package ru.practicum.services;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface MessageProducer {
    void sendUserAction(UserActionAvro userActionAvro);
}
