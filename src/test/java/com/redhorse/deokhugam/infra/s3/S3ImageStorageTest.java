package com.redhorse.deokhugam.infra.s3;

import com.redhorse.deokhugam.infra.s3.exception.S3UploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3ImageStorage Unit Test")
class S3ImageStorageTest
{
    @InjectMocks private S3ImageStorage s3ImageStorage;
    @Mock private S3Client s3Client;
    @Mock private S3Presigner s3Presigner;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3ImageStorage, "bucket", "test-bucket");
        ReflectionTestUtils.setField(s3ImageStorage, "expirationDurationInMinutes", 10L);
    }

    private MockMultipartFile createMockImage(String filename) {
        return new MockMultipartFile(
                "file", filename, "image/jpeg", "image-content".getBytes()
        );
    }

    @Nested
    @DisplayName("Upload 테스트")
    class Upload {
        @Test
        @DisplayName("성공 - 업로드 후 S3 키를 반환한다")
        void success_returnsS3Key() {
            MockMultipartFile file = createMockImage("cover.jpg");

            String key = s3ImageStorage.upload(file);

            assertThat(key).startsWith("thumbnail/");
            assertThat(key).endsWith(".jpg");
            verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        }

        @Test
        @DisplayName("성공 - 파일명이 null이면 확장자 없이 키를 반환한다")
        void success_withNullFilename_returnsKeyWithoutExtension() {
            MockMultipartFile file = new MockMultipartFile(
                    "file", null, "image/jpeg", "image-content".getBytes()
            );

            String key = s3ImageStorage.upload(file);

            assertThat(key).startsWith("thumbnail/");
        }

        @Test
        @DisplayName("실패 - IOException 발생 시 S3UploadException을 던진다")
        void fail_whenIOExceptionOccurs_throwsS3UploadException() throws IOException {
            MockMultipartFile file = spy(createMockImage("cover.jpg"));
            given(file.getBytes()).willThrow(new IOException("IO error"));

            assertThatThrownBy(() -> s3ImageStorage.upload(file))
                    .isInstanceOf(S3UploadException.class);
        }
    }

    @Nested
    @DisplayName("Presigned URL 테스트")
    class GeneratePresignedUrl {
        @Test
        @DisplayName("성공 - S3 키로 presigned URL 발급을 요청한다")
        void success_requestsPresignedUrl() {
            given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                    .willThrow(SdkClientException.create("not needed"));

            assertThatThrownBy(() -> s3ImageStorage.generatePresignedUrl("thumbnail/test.jpg"))
                    .isInstanceOf(SdkClientException.class);

            verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
        }
        @Test
        @DisplayName("성공 - key가 null이면 null을 반환한다")
        void success_withNullKey_returnsNull() {
            String url = s3ImageStorage.generatePresignedUrl(null);

            assertThat(url).isNull();
            verifyNoInteractions(s3Presigner);
        }

        @Test
        @DisplayName("성공 - key가 빈 문자열이면 null을 반환한다")
        void success_withBlankKey_returnsNull() {
            String url = s3ImageStorage.generatePresignedUrl("  ");

            assertThat(url).isNull();
            verifyNoInteractions(s3Presigner);
        }
    }
}