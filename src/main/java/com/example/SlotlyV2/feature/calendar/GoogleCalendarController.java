package com.example.SlotlyV2.feature.calendar;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/calendar/google")
@RequiredArgsConstructor
public class GoogleCalendarController {
    private final GoogleCalendarService calendarService;
    private final UserService userService;

}
