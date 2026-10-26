package org.bank.reportingservice.service;

import java.io.InputStream;

/**
 * Defines the contract for storing, retrieving and deleting report files.
 *
 * Implementations must document whether they consume or close input streams.
 */
public interface ReportStorage {

    /**
     * Stores a report in object storage.
     *
     * @param objectKey     the destination object key
     * @param content       the report content stream; ownership remains with
     *                      the caller, which must close it
     * @param contentLength the content length in bytes
     * @param contentType   the report content type
     * @return the stored object key
     */
    String store(
            String objectKey,
            InputStream content,
            long contentLength,
            String contentType
    );

    /**
     * Retrieves a report stream.
     *
     * The caller must close the returned stream, preferably with
     * try-with-resources.
     *
     * @param objectKey the source object key
     * @return the report content stream
     */
    InputStream retrieve(String objectKey);

    /**
     * Deletes a report from object storage.
     *
     * @param objectKey the object key to delete
     */
    void delete(String objectKey);
}
