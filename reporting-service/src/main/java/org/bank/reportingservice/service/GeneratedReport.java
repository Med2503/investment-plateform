package org.bank.reportingservice.service;

import java.io.InputStream;

public record GeneratedReport(
        InputStream content,
        long contentLength,
        String contentType,
        String fileExtension
) implements AutoCloseable {

    public GeneratedReport {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null");
        }
        if (contentLength < 0) {
            throw new IllegalArgumentException(
                    "Content length must not be negative"
            );
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException(
                    "Content type must not be blank"
            );
        }
        if (fileExtension == null || fileExtension.isBlank()) {
            throw new IllegalArgumentException(
                    "File extension must not be blank"
            );
        }
    }

    @Override
    public void close() throws java.io.IOException {
        content.close();
    }
}