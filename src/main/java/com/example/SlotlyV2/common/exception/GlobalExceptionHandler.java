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

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UsernameAlreadyExistsException.class,
            OptimisticLockException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleConflictExceptions(RuntimeException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            EventNotFoundException.class,
            SlotNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleNotFoundExceptions(RuntimeException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            UnauthorizedAccessException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorizeExceptions(RuntimeException ex) {
        return new ApiResponse<>(ex.getMessage(), null);
    }

    @ExceptionHandler({
            InvalidEventException.class,
            SlotNotBookedException.class,
            MaxCapacityExceededException.class,
            AccountAlreadyVerifiedException.class,
            AccountNotVerifiedException.class,
            InvalidTokenException.class,
            TokenAlreadyExpiredException.class,
            PasswordMismatchException.class,
            InvalidSlotException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequestExceptions(RuntimeException ex) {
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
