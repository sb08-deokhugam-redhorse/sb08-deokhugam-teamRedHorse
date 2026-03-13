package com.redhorse.deokhugam.global.batch.BatchConfig;

import com.redhorse.deokhugam.global.batch.repository.UserBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class UserCleanupBatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final UserBatchRepository userBatchRepository;
  private final CacheManager cacheManager;

  // Job 설정
  @Bean
  public Job cleanupUserJob() {
    return new JobBuilder("cleanupUserJob", jobRepository)
        .start(deleteOldUsersStep())
        .build();
  }

  // Step 설정 - 현재 1개. 여러개가 될 수 있음
  @Bean
  public Step deleteOldUsersStep() {
    return new StepBuilder("deleteOldUsersStep", jobRepository)
        .tasklet(deleteOldUsersTasklet(), transactionManager)
        .build();
  }

  // 3. Tasklet 로직
  private Tasklet deleteOldUsersTasklet() {
    // RetryTemplate: DB 락(Lock) 등으로 일시적 실패 시 3번까지 재시도합니다.
    RetryTemplate retryTemplate = new RetryTemplateBuilder()
        .maxAttempts(3)
        .fixedBackoff(2000)
        .build();

    return (contribution, chunkContext) -> {

      int deletedRows = retryTemplate.execute(context ->
          userBatchRepository.deleteSoftDeletedUsersInBulk());

      // 삭제된 유저가 있다면 캐시 무효화
      if (deletedRows > 0) {
        var cache = cacheManager.getCache("getUser");
        if (cache != null) {
          cache.clear();
        }
      }

      // batch_step_execution테이블에 기록함
      // batch_step_execution → write_count 컬럼에 누적 숫자로 저장
      contribution.getStepExecution().setWriteCount(
          contribution.getStepExecution().getWriteCount() + deletedRows
      );

      return deletedRows > 0 ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
    };
  }
}