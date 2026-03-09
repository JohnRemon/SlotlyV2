package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.schedule.BlockedPeriodNotFoundException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodRequest;
import com.example.SlotlyV2.feature.schedule.dto.BlockedPeriodResponse;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockedPeriodService {

    private final BlockedPeriodRepository blockedPeriodRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public BlockedPeriodResponse getBlockedPeriod(UUID id) {
        return toResponse(blockedPeriodRepository.findById(id)
                .orElseThrow(() -> new BlockedPeriodNotFoundException("Blocked period not found with id: " + id)));
    }

    @Transactional(readOnly = true)
    public List<BlockedPeriodResponse> getBlockedPeriods() {
        User currentUser = userService.getCurrentUser();
        return blockedPeriodRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BlockedPeriodResponse createBlockedPeriod(BlockedPeriodRequest request) {
        User currentUser = userService.getCurrentUser();

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidScheduleException("End time must be after start time");
        }

        boolean overlaps = !blockedPeriodRepository
                .findByUserIdAndEndTimeAfterAndStartTimeBefore(
                        currentUser.getId(), request.getStartTime(), request.getEndTime())
                .isEmpty();

        if (overlaps) {
            throw new InvalidScheduleException("Time block overlaps with an existing blocked period");
        }

        BlockedPeriod blockedPeriod = BlockedPeriod.builder()
                .user(currentUser)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .reason(request.getReason())
                .isRecurring(request.isRecurring())
                .build();

        log.info("Blocked period created userId={} start={} end={}",
                currentUser.getId(), request.getStartTime(), request.getEndTime());
        return toResponse(blockedPeriodRepository.save(blockedPeriod));
    }

    private BlockedPeriodResponse toResponse(BlockedPeriod blockedPeriod) {
        return new BlockedPeriodResponse(blockedPeriod);
    }
}
