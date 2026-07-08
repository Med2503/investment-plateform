package org.bank.accountservice.exception.GlobalExceptionHandler;


import jakarta.servlet.http.HttpServletRequest;
import org.bank.accountservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InitialDepositException.class)
    public ResponseEntity<ErrorResponse> handleInitialDeposit(InitialDepositException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Invalid deposit", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccountCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleCurrency(AccountCurrencyException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_CURRENCY", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(AccountAlreadyExistsByTypeException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccount(AccountAlreadyExistsByTypeException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "ACCOUNT_ALREADY_EXISTS", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccount(AccountNotFoundException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "ACCOUNT_NOT_FOUND", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsuffieantBalance(InsufficientBalanceException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE.value(), "INSUFFISANT-BALANCE", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_ACCEPTABLE.value(), "INVALID-AMOUNT", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
    }

    @ExceptionHandler(AccountExistsByUserIdAndType.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(AccountExistsByUserIdAndType ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(LocalDateTime.now(), HttpStatus.EXPECTATION_FAILED.value(), "ACCOUNT-EXITS", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
    }


    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        "OPTIMISTICL_LOCK_ERROR",
                        ex.getMessage(),
                        request.getRequestURI()));


    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handler(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "INTERNAL_ERROR",
                        ex.getMessage(),
                        request.getRequestURI()));

    }


}
