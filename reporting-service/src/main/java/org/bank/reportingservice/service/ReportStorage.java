package org.bank.reportingservice.service;

import java.io.InputStream;

public interface ReportStorage {

    String store(
            String objectKey,
            InputStream content,
            long contentLength,
            String contentType
    );

    InputStream retrieve(String objectKey);

    void delete(String objectKey);
}