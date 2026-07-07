package org.bank.accountservice.exception.GlobalExceptionHandler;


import jakarta.servlet.http.HttpServletRequest;
import org.bank.accountservice.exception.AccountAlreadyExistsByTypeException;
import org.bank.accountservice.exception.AccountCurrencyException;
import org.bank.accountservice.exception.InitialDepositException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
