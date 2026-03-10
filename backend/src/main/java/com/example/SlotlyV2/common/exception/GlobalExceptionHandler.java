package com.example.SlotlyV2.common.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.SlotlyV2.common.exception.auth.AccountAlreadyVerifiedException;
import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.ForbiddenException;
import com.example.SlotlyV2.common.exception.auth.GoogleOAuth2Exception;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.RateLimitExceededException;
import com.example.SlotlyV2.common.exception.auth.TokenAlreadyExpiredException;
import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.booking.BookingNotFoundException;
import com.example.SlotlyV2.common.exception.booking_form.BookingFormNotFoundException;
import com.example.SlotlyV2.common.exception.booking_form.InvalidFormResponseException;
import com.example.SlotlyV2.common.exception.booking_form.QuestionNotFoundException;
import com.example.SlotlyV2.common.exception.calendar.GoogleCalendarException;
import com.example.SlotlyV2.common.exception.calendar.GoogleCalendarNotConnectedException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.exception.user.UserAlreadyExistsException;
import com.example.SlotlyV2.common.exception.user.UserNotFoundException;
import com.example.SlotlyV2.common.exception.user.UsernameAlreadyExistsException;
import com.example.SlotlyV2.common.dto.ErrorResponse;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 404 not found ─────────────────────────────────────────────────────────
    @ExceptionHandler({
            UserNotFoundException.class,
            EventNotFoundException.class,
            SlotNotFoundException.class,
            ScheduleNotFoundException.class,
            BookingNotFoundException.class,
            BookingFormNotFoundException.class,
            QuestionNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "NOT_FOUND", HttpStatus.NOT_FOUND);
    }

    // ── 401 Unauthorized ──────────────────────────────────────────────────────
    @ExceptionHandler({
            InvalidCredentialsException.class,
            UnauthorizedAccessException.class,
            GoogleOAuth2Exception.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    // ── 403 Forbidden ──────────────────────────────────────────────────────
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    // ── 409 Conflict ──────────────────────────────────────────────────────────
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            SlotAlreadyBookedException.class,
            OptimisticLockException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "CONFLICT", HttpStatus.CONFLICT);
    }

    // ── 400 Bad Request ───────────────────────────────────────────────────────
    @ExceptionHandler({
            InvalidEventException.class,
            SlotNotBookedException.class,
            MaxCapacityExceededException.class,
            AccountAlreadyVerifiedException.class,
            AccountNotVerifiedException.class,
            InvalidTokenException.class,
            TokenAlreadyExpiredException.class,
            InvalidSlotException.class,
            InvalidScheduleException.class,
            InvalidFormResponseException.class,
            GoogleCalendarNotConnectedException.class,
            GoogleCalendarException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    // ── 400 Validation ────────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (first, second) -> first));

        return ErrorResponse.of(
                "Validation failed",
                request.getRequestURI(),
                "VALIDATION_ERROR",
                HttpStatus.BAD_REQUEST,
                details);
    }

    // ── 429 Rate Limit ────────────────────────────────────────────────────────
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleRateLimit(RateLimitExceededException ex, HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        response.setHeader("X-RateLimit-Limit", "true");
        log.warn("Rate limit exceeded path={} message={}", request.getRequestURI(), ex.getMessage());
        return ErrorResponse.of(ex.getMessage(), request.getRequestURI(), "RATE_LIMIT_EXCEEDED",
                HttpStatus.TOO_MANY_REQUESTS);
    }

    // ── 500 Internal Server Error ─────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception path={} message={}", request.getRequestURI(), ex.getMessage(), ex);
        return ErrorResponse.of(
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI(),
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
