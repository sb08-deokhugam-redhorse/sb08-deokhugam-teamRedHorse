package com.redhorse.deokhugam.global.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalBatchScheduler {

    // 배치 실행 런처
    private final JobLauncher jobLauncher;

    // 작성한 배치 Config에서 @Bean으로 등록한 Job을 주입
    // 메서드 이름과 동일하게 변수명을 지으면 자동으로 매핑
    private final Job cleanupAlarmJob;
    private final Job reivewRankingBatchJob;
    private final Job bookRankingBatchJob;
    private final Job userRankingBatchJob;
    private final Job cleanupUserJob;

    /**
     * cron 표현식: (초 분 시 일 월 요일)
     *  첫 번째 *: 초
     *  두 번째 *: 분
     *  세 번째 *: 시 (24시 표기)
     *  네 번째 *: 일
     * 다섯번째 *: 월
     * 여섯번째 *: 요일
     */

    // 매일 오전 1시 30분에 동작하게
    @Scheduled(cron = "0 30 1 * * *", zone = "Asia/Seoul")
    public void runDeleteAlarmJob() {
        log.info("[Alarm-Batch] 작업 시작: 오전 1시 예약된 작업 진행");

        // thread Pool이 아니면 순차적으로 실행됨
        runJob(cleanupAlarmJob);
        runJob(cleanupUserJob);
    }

    // 매일 오전 2시에 동작하게
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void runDailyDashboardJob() {
        log.info("[DoashDoard-Batch] 작업 시작: 오전 2시 예약된 작업 진행");

        runJob(reivewRankingBatchJob);
        runJob(bookRankingBatchJob);
        runJob(userRankingBatchJob);
    }

    private void runJob(Job job) {
        try {
            // 실행 시점마다 고유한 파라미터 생성
            JobParameters params = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // thread Pool이 아니면 순차적으로 실행됨
            log.info("[{}] 배치 시작", job.getName());
            jobLauncher.run(job, params);

        } catch (JobExecutionAlreadyRunningException e) {
            /**
             * 데이터가 많아 작업이 늦어져 다른 배치의 실행 시간이 된다면
             * 다른 배치에서 출동 에러가 나옴 이건 진행 중인 배치를
             * 돌리고 새로 시작된 배치를 건너뛰게됨
             * 문제는 건너 뛰는거라 새 배치가 동작이 안될 수 있음
             * 나중에 배치가 많아 진다면 thread Pool로 병렬처리 가능
             */

            log.warn("[Batch-{}] 배치가 이미 실행 중이므로 이번 스케줄은 건너뜁니다.", job.getName());
        } catch (Exception e) {
            log.error("[Batch-{}] 배치 실행 중 치명적 에러 발생: {}", job.getName(), e.getMessage());
        }
    }
}