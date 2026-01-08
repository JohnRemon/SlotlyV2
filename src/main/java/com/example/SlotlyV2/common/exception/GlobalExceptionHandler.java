package com.example.SlotlyV2.common.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.exception.auth.AccountAlreadyVerifiedException;
import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.PasswordMismatchException;
import com.example.SlotlyV2.common.exception.auth.RateLimitExceededException;
import com.example.SlotlyV2.common.exception.auth.TokenAlreadyExpiredException;
import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotNotBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.exception.user.UserAlreadyExistsException;
import com.example.SlotlyV2.common.exception.user.UserNotFoundException;
import com.example.SlotlyV2.common.exception.user.UsernameAlreadyExistsException;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleUserNotFound(UserNotFoundException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleEventNotFound(EventNotFoundException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidEventException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidEvent(InvalidEventException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(SlotNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleSlotNotFound(SlotNotFoundException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(SlotNotBookedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleSlotNotBooked(SlotNotBookedException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(MaxCapacityExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMaxCapacityExceeded(MaxCapacityExceededException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(OptimisticLockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleOptimisticLock(OptimisticLockException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleAccountNotVerified(AccountNotVerifiedException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidToken(InvalidTokenException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(TokenAlreadyExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleTokenAlreadyExpired(TokenAlreadyExpiredException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handlePasswordMismatch(PasswordMismatchException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(InvalidSlotException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleInvalidSlot(InvalidSlotException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ApiResponse<Void> handleRateLimitExceeded(RateLimitExceededException ex, HttpServletResponse response) {
        response.setHeader("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        response.setHeader("X-Rate-Limit-Exceeded", "true");
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {} for user {}:  {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex);
        return new ApiResponse<>("This is an error from our side, please try again later", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        return new ApiResponse<>("Invalid Arguments", errors);
    }
}
