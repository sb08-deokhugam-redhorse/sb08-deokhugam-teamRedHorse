package com.redhorse.deokhugam.infra.s3.scheduler;

import com.redhorse.deokhugam.infra.s3.S3LogStorage;
import com.redhorse.deokhugam.infra.s3.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogUploadScheduler
{
    @Value("${logging.file.path:.logs}")
    private String logDir;

    private final S3LogStorage s3LogStorage;

    private static final String FAILED_UPLOADS_FILE = "failed_uploads.txt";

    /**
     * 로그 업로드 실패 처리 전략
     * 1. 파일 저장 방식 (단순, 단일 인스턴스에서만 사용)
     * 2. DB 저장 + 재시도 스케줄러 (기본)
     */
    @Scheduled(cron = "0 0 1 * * *")
    // @Scheduled(cron = "0 * * * * *")
    public void uploadLog() {
        // 1. 이전 실패 목록 재시도
        try {
            retryFailedUploads();
        } catch (IOException e) {
            log.error("[Log-Scheduler] 실패 목록 재시도 작업 실패: ", e);
        }

        // 2. 전날 로그 업로드
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 10MB마다 분할되도록 설정 -> 전날 날짜에 해당하는 모든 파일
        try (Stream<Path> files = Files.list(Paths.get(logDir))) {
            files.filter(path -> path.getFileName().toString().contains(yesterday))
                    .forEach(this::uploadWithFailureChecking);
        } catch (IOException e) {
            log.error("[Log-Scheduler] 로그 파일 업로드 작업 실패: {}", e.getMessage());
        }
    }

    /**
     * 이전 업로드 실패 목록으로 업로드를 재시도한다.
     */
    private void retryFailedUploads() throws IOException {
        Path failedFile = Paths.get(logDir, FAILED_UPLOADS_FILE);

        if (!Files.exists(failedFile)) return;

        // 실패했던 로그 전부 모음
        List<String> failedPaths;
        try {
            failedPaths = new ArrayList<>(Files.readAllLines(failedFile));
        } catch (IOException e) {
            log.error("[Log-Scheduler] 실패 목록 파일 읽기 실패: {}", e.getMessage());
            return;
        }

        // 재시도 후 성공한 path만 제거
        List<String> stillFailed = new ArrayList<>();
        for (String pathStr : failedPaths) {
            try {
                s3LogStorage.upload(Paths.get(pathStr));
                log.info("[Log-Scheduler] 재업로드 성공: path={}", pathStr);
            } catch (S3UploadException e) {
                log.error("[Log-Scheduler] 재업로드 실패: path={}", pathStr);
                stillFailed.add(pathStr);  // 실패한 것만 유지
            }
        }

        if (stillFailed.isEmpty()) {
            Files.delete(failedFile);
        } else {
            Files.write(failedFile, stillFailed);  // 실패한 것만 덮어쓰기
        }
    }

    /**
     * 로그 파일을 업로드하고 실패 시 failed_uploads.txt에 저장한다.
     *
     * @param path 업로드할 로그 파일 경로
     */
    private void uploadWithFailureChecking(Path path) {
        try {
            s3LogStorage.upload(path);
        } catch (S3UploadException e) {
            log.error("[Log-Scheduler] 로그 파일 업로드 실패, 실패 목록에 저장: path={}", path);
            saveFailedPath(path);
        }
    }

    /**
     * 업로드에 실패한 파일 경로를 failed_uploads.txt에 저장한다.
     *
     * @param path 저장할 실패 파일 경로
     */
    private void saveFailedPath(Path path) {
        Path failedFile = Paths.get(logDir, FAILED_UPLOADS_FILE);

        try {
            Files.writeString(failedFile, path + System.lineSeparator(), // \n
                    StandardOpenOption.CREATE,  // 없으면 생성
                    StandardOpenOption.APPEND); // 있으면 끝에 추가 (줄마다 누적)
        } catch (IOException e) {
            log.error("[Log-Scheduler] 실패 목록 저장 실패: path={}", path);
        }
    }
}
