package com.redhorse.deokhugam.infra.s3;

import com.redhorse.deokhugam.infra.s3.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3LogStorage
{
    @Value("${storage.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;

    public void upload(Path logFile) {
        String key = "logs/" + logFile.getFileName().toString();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType("text/plain")
                            .build(),
                    RequestBody.fromFile(logFile)
            );

            log.info("[S3-Log] S3 로그 업로드 작업 완료: key={}", key);
        } catch (S3Exception e) {
            log.error("[S3-Log] S3 로그 업로드 작업 실패: Key={}", key);
            throw new S3UploadException(e);
        }
    }
}
