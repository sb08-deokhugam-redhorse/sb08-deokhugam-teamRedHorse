package com.redhorse.deokhugam.infra.s3;

import com.redhorse.deokhugam.infra.s3.exception.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3LogStorage Unit Test")
class S3LogStorageTest
{
    @InjectMocks private S3LogStorage s3LogStorage;
    @Mock private S3Client s3Client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3LogStorage, "bucket", "test-bucket");
    }

    @Nested
    @DisplayName("Upload 테스트")
    class Upload {
        @Test
        @DisplayName("성공 - 로그 파일을 S3에 업로드한다")
        void success_uploadsLogFile(@TempDir Path tempDir) throws IOException {
            Path logFile = tempDir.resolve("deokhugam-2026-03-08.0.log");
            Files.writeString(logFile, "log content");

            s3LogStorage.upload(logFile);

            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("실패 - S3 업로드 실패 시 S3UploadException을 던진다")
        void fail_whenS3Fails_throwsS3UploadException(@TempDir Path tempDir) throws IOException {
            Path logFile = tempDir.resolve("deokhugam-2026-03-08.0.log");
            Files.writeString(logFile, "log content");

            given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                    .willThrow(S3Exception.builder().message("S3 error").build());

            assertThatThrownBy(() -> s3LogStorage.upload(logFile))
                    .isInstanceOf(S3UploadException.class);
        }
    }
}