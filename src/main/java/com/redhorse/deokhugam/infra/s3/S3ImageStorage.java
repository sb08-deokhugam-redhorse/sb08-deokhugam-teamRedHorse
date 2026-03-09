package com.redhorse.deokhugam.infra.s3;

import com.redhorse.deokhugam.infra.s3.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageStorage
{
    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${storage.s3.expiration-duration-in-minutes:10}")
    private long expirationDurationInMinutes;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    /**
     * 이미지 파일을 S3에 업로드한다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 S3 키
     * @throws S3UploadException S3 업로드 실패 시
     */
    public String upload(MultipartFile file) {
        String key = "thumbnail/" + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException | SdkException e) {
            throw new S3UploadException(e);
        }

        return key;
    }

    /**
     * S3 키로 presigned URL을 발급한다.
     *
     * @param key S3 키
     * @return 만료 시간 동안 유효한 presigned URL
     */
    @Named("toPresignedUrl")
    public String generatePresignedUrl(String key) {
        if (key == null || key.isBlank()) return null;

        try {
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationDurationInMinutes))
                    .getObjectRequest(r -> r.bucket(bucket).key(key))
                    .build();

            return s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();
        } catch (Exception e) {
            log.warn("[S3-Api] Presigned URL 발급 실패: key={}", key, e);
            return null;
        }
    }


    /**
     * 확장자를 추출한다.
     *
     * @param fileName 추출할 파일명
     * @return 추출된 확장자
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";

        return fileName.substring(fileName.lastIndexOf("."));
    }
}
