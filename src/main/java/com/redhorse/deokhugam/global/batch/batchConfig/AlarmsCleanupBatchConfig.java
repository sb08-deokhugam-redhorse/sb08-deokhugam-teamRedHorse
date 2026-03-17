package com.redhorse.deokhugam.global.batch.batchConfig;

import com.redhorse.deokhugam.global.batch.repository.AlarmBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AlarmsCleanupBatchConfig {

    /**
     * 배치에서 제공하는 bean들
     * JobRepository: 배치 실행 기록,
     * 실패 지점 기억해 이어서 동작하게 해줌,
     * 실행 기록있으면 추가 실행 멈춤
     * PlatformTransactionManager
     * 트랜잭션 관리, JobRepository의 삑사리 대비용 겸
     * 특정 버전 이후는 필수
     */
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 배치에 쓸 레포짓
    private final AlarmBatchRepository alarmRepository;

    @Bean
    public Job cleanupAlarmJob() {
        return new JobBuilder("cleanupAlarmJob", jobRepository)
                .start(deleteOldAlarmsStep())
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {

                        if (jobExecution.getStatus() == BatchStatus.FAILED) {
                            log.error("[Alarm-batch] 에러 <배치 실행 중 에러 발생>: detail = {}",
                                    jobExecution.getAllFailureExceptions());

                        } else if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("[Alarm-batch] 작업 완료: {} 건 삭제",
                                    jobExecution.getStepExecutions().iterator().next().getWriteCount());
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step deleteOldAlarmsStep() {
        return new StepBuilder("deleteOldAlarmsStep", jobRepository)
                .tasklet(deleteOldAlarmsTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet deleteOldAlarmsTasklet() {
        // 최대 3번 재시도, 재시도 간격 2초 (2000ms) 설정
        RetryTemplate retryTemplate = new RetryTemplateBuilder()
                .maxAttempts(3)
                .fixedBackoff(2000)
                .retryOn(Exception.class) // 어느 에러에서 재동작할지, 여러개 선언 가능
                .build();

        return (contribution, chunkContext) -> {
            int chunkSize = 1000; // 한 번에 처리할 건수

            // DB 삭제 로직을 RetryTemplate으로 감쌈
            int deletedRows = retryTemplate.execute(context ->
                    alarmRepository.deleteOldAlarmsInBulk(chunkSize));

            // 지운 건수를 누적 기록
            contribution.getStepExecution().setWriteCount(
                    contribution.getStepExecution().getWriteCount() + deletedRows
            );

            // 지운게 있다면 -> 현재 트랜잭션 커밋하고 Tasklet 다시 실행, RepeatStatus.CONTINUABLE
            // 지울 게 없다면 -> Tasklet 종료, RepeatStatus.FINISHED
            return deletedRows > 0 ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
        };
    }
}