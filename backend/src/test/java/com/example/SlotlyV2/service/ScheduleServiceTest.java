// package com.example.SlotlyV2.service;
//
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.when;
//
// import java.time.LocalTime;
// import java.time.OffsetDateTime;
// import java.util.Collections;
// import java.util.Optional;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.example.SlotlyV2.feature.calendar.GoogleCalendarService;
// import com.example.SlotlyV2.feature.schedule.BlockedPeriodRepository;
// import com.example.SlotlyV2.feature.schedule.DailySchedule;
// import com.example.SlotlyV2.feature.schedule.DailyScheduleRepository;
// import com.example.SlotlyV2.feature.schedule.ScheduleService;
// import com.example.SlotlyV2.feature.user.User;
//
// @ExtendWith(MockitoExtension.class)
// class ScheduleServiceTest {
//
// @Mock
// private DailyScheduleRepository dailyScheduleRepository;
//
// @Mock
// private BlockedPeriodRepository blockedPeriodRepository;
//
// @Mock
// private GoogleCalendarService googleCalendarService;
//
// @InjectMocks
// private ScheduleService scheduleService;
//
// private User testUser;
// private DailySchedule tuesdaySchedule;
//
// @BeforeEach
// void setUp() {
// testUser = User.builder()
// .id(1L)
// .email("test@example.com")
// .timeZone("Europe/Berlin") // ADDED: User's timezone
// .build();
//
// // Feb 3, 2026 is a Tuesday (dayOfWeek = 2)
// tuesdaySchedule = DailySchedule.builder()
// .id(UUID.randomUUID())
// .user(testUser)
// .dayOfWeek(2) // Tuesday
// .isAvailable(true)
// .startTime(LocalTime.of(9, 0)) // 9 AM Berlin time
// .endTime(LocalTime.of(17, 0)) // 5 PM Berlin time
// .build();
// }
//
// @Test
// void shouldRejectMidnightSlot() {
// // Given: Tuesday schedule is 9 AM - 5 PM Berlin time
// when(dailyScheduleRepository.findByUserIdAndDayOfWeek(testUser.getId(), 2))
// .thenReturn(Optional.of(tuesdaySchedule));
//
// // When: Checking a slot at midnight Berlin time (23:00 UTC on Feb 2)
// OffsetDateTime midnight = OffsetDateTime.parse("2026-02-02T23:00:00Z"); //
// Midnight in Berlin
// boolean isValid = scheduleService.isValidSlot(testUser, midnight, 60);
//
// // Then: Should be rejected
// assertFalse(isValid, "Midnight slot should be rejected as it's outside 9 AM -
// 5 PM schedule");
// }
//
// @Test
// void shouldRejectSlotEndingAfterScheduleEnd() {
// // Given: Tuesday schedule is 9 AM - 5 PM Berlin time
// when(dailyScheduleRepository.findByUserIdAndDayOfWeek(testUser.getId(), 2))
// .thenReturn(Optional.of(tuesdaySchedule));
//
// // When: Checking a slot at 4:30 PM Berlin time (ends at 5:30 PM, after
// schedule
// // end)
// OffsetDateTime fourThirtyPM = OffsetDateTime.parse("2026-02-03T15:30:00Z");
// // 4:30 PM Berlin time
// boolean isValid = scheduleService.isValidSlot(testUser, fourThirtyPM, 60);
//
// // Then: Should be rejected (ends at 5:30 PM Berlin time, after 5 PM cutoff)
// assertFalse(isValid, "Slot ending at 5:30 PM should be rejected (schedule
// ends at 5 PM)");
// }
//
// @Test
// void shouldAcceptValidMorningSlot() {
// // Given: Tuesday schedule is 9 AM - 5 PM Berlin time
// when(dailyScheduleRepository.findByUserIdAndDayOfWeek(testUser.getId(), 2))
// .thenReturn(Optional.of(tuesdaySchedule));
//
// when(blockedPeriodRepository.findByUserIdAndEndTimeAfterAndStartTimeBefore(
// eq(testUser.getId()), any(), any()))
// .thenReturn(Collections.emptyList());
//
// when(googleCalendarService.getUpcomingEvents(eq(testUser), any(), any()))
// .thenReturn(Collections.emptyList());
//
// // When: Checking a valid slot at 10 AM Berlin time (9:00 UTC)
// OffsetDateTime tenAM = OffsetDateTime.parse("2026-02-03T09:00:00Z"); // 10 AM
// Berlin time
// boolean isValid = scheduleService.isValidSlot(testUser, tenAM, 60);
//
// // Then: Should be accepted
// assertTrue(isValid, "10 AM slot should be valid");
// }
//
// @Test
// void shouldRejectSlotOnUnavailableDay() {
// // Given: Saturday is unavailable
// DailySchedule saturdaySchedule = DailySchedule.builder()
// .id(UUID.randomUUID())
// .user(testUser)
// .dayOfWeek(6)
// .isAvailable(false)
// .startTime(LocalTime.of(0, 0))
// .endTime(LocalTime.of(0, 0))
// .build();
//
// when(dailyScheduleRepository.findByUserIdAndDayOfWeek(testUser.getId(), 6))
// .thenReturn(Optional.of(saturdaySchedule));
//
// // When: Checking a slot on Saturday Feb 7 at 10 AM Berlin time
// OffsetDateTime saturday = OffsetDateTime.parse("2026-02-07T09:00:00Z"); // 10
// AM Berlin time
// boolean isValid = scheduleService.isValidSlot(testUser, saturday, 60);
//
// // Then: Should be rejected
// assertFalse(isValid, "Saturday slot should be rejected (day unavailable)");
// }
// }
