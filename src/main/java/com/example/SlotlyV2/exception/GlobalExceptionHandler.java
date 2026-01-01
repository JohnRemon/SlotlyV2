package com.example.SlotlyV2.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.SlotlyV2.dto.ApiResponse;

import jakarta.persistence.OptimisticLockException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEventNotFound(EventNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidEventException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEvent(InvalidEventException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(SlotNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleSlotNotFound(SlotNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(SlotNotBookedException.class)
    public ResponseEntity<ApiResponse<Void>> handleSlotNotBooked(SlotNotBookedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(MaxCapacityExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxCapacityExceeded(MaxCapacityExceededException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(OptimisticLockException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(AccountAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountAlreadyVerified(AccountAlreadyVerifiedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotVerified(AccountNotVerifiedException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(TokenAlreadyExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleTokenAlreadyExpired(TokenAlreadyExpiredException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handlePasswordMismatch(PasswordMismatchException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidSlotException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSlot(InvalidSlotException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>(ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .internalServerError()
                .body(new ApiResponse<>("This is an error from our side, please try again later", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        ApiResponse<List<String>> response = new ApiResponse<>("Invalid Arguments", errors);

        return ResponseEntity.badRequest().body(response);
    }
}
