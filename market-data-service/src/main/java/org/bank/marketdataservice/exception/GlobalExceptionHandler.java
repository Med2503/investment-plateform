package org.bank.marketdataservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MarketAssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> handleNotFound(
            MarketAssetNotFoundException ex
    ){

        return Map.of(
                "error",
                ex.getMessage()
        );

    }





    @ExceptionHandler(MarketAssetAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String,String> handleAlreadyExists(
            MarketAssetAlreadyExistsException ex
    ){

        return Map.of(
                "error",
                ex.getMessage()
        );

    }





    @ExceptionHandler(InvalidMarketPriceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleInvalidPrice(
            InvalidMarketPriceException ex
    ){

        return Map.of(
                "error",
                ex.getMessage()
        );

    }





    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,String> handleGeneric(
            Exception ex
    ){

        return Map.of(
                "error",
                ex.getMessage()
        );

    }


}