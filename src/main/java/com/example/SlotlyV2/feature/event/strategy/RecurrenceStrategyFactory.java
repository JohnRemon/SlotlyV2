package com.example.SlotlyV2.feature.event.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecurrenceStrategyFactory {
    private final Map<String, RecurrenceStrategy> strategies;

    public RecurrenceStrategy getStrategy(String strategyType) {
        RecurrenceStrategy strategy = strategies.get(strategyType);

        if (strategy == null) {
            throw new IllegalArgumentException("Strategy type not found");
        }

        return strategy;
    }
}
