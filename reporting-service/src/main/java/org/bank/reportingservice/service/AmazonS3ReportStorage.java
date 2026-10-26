package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.exception.ReportStorageException;
import org.bank.reportingservice.service.ReportStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AmazonS3ReportStorage implements ReportStorage {

    private final S3Client s3Client;

    @Value("${reporting.storage.s3.bucket}")
    private String bucket;

    @Override
    public String store(
            String objectKey,
            InputStream content,
            long contentLength,
            String contentType
    ) {
        validateConfiguration();
        validateObjectKey(objectKey);

        if (content == null) {
            throw new IllegalArgumentException(
                    "Content must not be null"
            );
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

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(content, contentLength)
            );

            return objectKey;

        } catch (S3Exception | SdkClientException exception) {
            throw new ReportStorageException(
                    "Unable to store report in object storage",
                    exception
            );
        }
    }

    @Override
    public InputStream retrieve(String objectKey) {
        validateConfiguration();
        validateObjectKey(objectKey);

        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            ResponseInputStream<GetObjectResponse> response =
                    s3Client.getObject(request);

            return response;

        } catch (S3Exception | SdkClientException exception) {
            throw new ReportStorageException(
                    "Unable to retrieve report from object storage",
                    exception
            );
        }
    }

    @Override
    public void delete(String objectKey) {
        validateConfiguration();
        validateObjectKey(objectKey);

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(request);

        } catch (S3Exception | SdkClientException exception) {
            throw new ReportStorageException(
                    "Unable to delete report from object storage",
                    exception
            );
        }
    }

    private void validateConfiguration() {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException(
                    "Report storage S3 bucket must not be blank"
            );
        }
    }

    private void validateObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank"
            );
        }
    }
}
