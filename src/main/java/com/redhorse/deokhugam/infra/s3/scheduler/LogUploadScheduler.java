package com.redhorse.deokhugam.infra.s3.scheduler;

import com.redhorse.deokhugam.infra.s3.S3LogStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogUploadScheduler
{
    @Value("${logging.file.path:.logs}")
    private String logDir;

    private final S3LogStorage s3LogStorage;

    @Scheduled(cron = "0 1 0 * * *")
    // @Scheduled(cron = "0 * * * * *")
    public void uploadLog() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 10MB마다 분할되도록 설정 -> 전날 날짜에 해당하는 모든 파일
        try (Stream<Path> files = Files.list(Paths.get(logDir))) {
            files.filter(path -> path.getFileName().toString().contains(yesterday))
                    .forEach(s3LogStorage::upload);
        } catch (IOException e) {
            log.error("[Scheduler] 로그 파일 조회 실패: {}", e.getMessage());
        }
    }
}
