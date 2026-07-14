package org.bank.portfolioservice.exception.Handler;

import org.bank.portfolioservice.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PortfolioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePortfolioNotFound(
            PortfolioNotFoundException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PortfolioAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlePortfolioAlreadyExists(
            PortfolioAlreadyExistsException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AssetAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleAssetAlreadyExists(
            AssetAlreadyExistsException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(InvalidQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidQuantity(
            InvalidQuantityException ex
    ) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(PortfolioAssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAssetNotFound(
            PortfolioAssetNotFoundException ex
    ) {
        return Map.of("error", ex.getMessage());
    }
}
