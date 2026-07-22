package org.bank.tradingservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TradeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> tradeNotFound(
            TradeNotFoundException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidTradeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidTrade(
            InvalidTradeException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> generic(
            Exception ex
    ) {
        return Map.of("error", ex.getMessage());
    }

}