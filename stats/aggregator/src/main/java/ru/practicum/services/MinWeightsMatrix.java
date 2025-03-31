package ru.practicum.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MinWeightsMatrix {
    private final Map<Long, Map<Long, Double>> minWeightsSum = new ConcurrentHashMap<>();

    public void put(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        minWeightsSum.computeIfAbsent(first, k -> new HashMap<>()).put(second, sum);
    }

    public double get(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        return minWeightsSum.getOrDefault(first, Map.of())
                .getOrDefault(second, 0.0);
    }
}
