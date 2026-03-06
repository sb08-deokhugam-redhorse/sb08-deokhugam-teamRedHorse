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
    public void runJob() {
        try {
            log.info("[batch] 작업 시작: 오전 1시 예약된 작업 진행");
            /**
             * JobParameters: 배치 실행 시 넘겨주는 파라미터
             * ※ 중요: 스프링 배치는 동일한 파라미터로 다시 실행하는 것을 '중복 실행'으로 간주해 막아버림
             * 매번 실행할 때마다 현재 시간(System.currentTimeMillis)을
             * 파라미터로 넣어 '새로운 작업'임을 인식
             */
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            // 배치 실행
            jobLauncher.run(cleanupAlarmJob, jobParameters);

        } catch (JobExecutionAlreadyRunningException e) {
            /**
             * 알림이 많아 작업이 늦어져 다른 배치의 실행 시간이 된다면
             * 다른 배치에서 출동 에러가 나옴 이건 진행 중인 배치를
             * 돌리고 새로 시작된 배치를 건너뛰게됨
             * 문제는 건너 뛰는거라 새 배치가 동작이 안될 수 있음
             * 나중에 배치가 많아 진다면 queue로 대기열 만들기 가능하다함, 일단 스킵
             */

            String jobName = cleanupAlarmJob.getName(); // 현재 진행 중인 배치

            log.warn("[batch] {}배치가 실행 중, 이번 스케줄은 건너뜁니다.", jobName);
        } catch (Exception e) {
            log.error("[batch] 에러 <배치 실행 중 에러 발생>: detail = {}", e.getMessage());
        }
    }
}