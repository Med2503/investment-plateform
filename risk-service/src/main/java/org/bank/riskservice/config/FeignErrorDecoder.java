package org.bank.riskservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.bank.riskservice.exception.ExternalServiceException;

public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new ExternalServiceException("BAD REQUEST");
            case 401 -> new ExternalServiceException("UNAUTHORIZED");
            case 403 -> new ExternalServiceException("FORBIDDEN");
            case 404 -> new ExternalServiceException("NOT FOUND");
            case 500 -> new ExternalServiceException("SERVER ERROR");
            case 503 -> new ExternalServiceException("UNAVAILABLE");
            default -> new ExternalServiceException("EXTERNAL ERROR " + response.status());
        };

    }
}
