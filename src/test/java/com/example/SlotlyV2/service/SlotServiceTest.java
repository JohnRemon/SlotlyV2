package com.example.SlotlyV2.service;

import static org.mockito.Mockito.reset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.example.SlotlyV2.repository.EventRepository;
import com.example.SlotlyV2.repository.SlotRepository;

@ExtendWith(MockitoExtension.class)
public class SlotServiceTest {

    @Mock
    private SlotRepository slotRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SlotService slotService;

    @BeforeEach
    void setUp() {
        reset(slotRepository, eventRepository, eventPublisher);
    }

    @Test
    void shouldGenerate4SlotsSuccessfully() {

    }

}
