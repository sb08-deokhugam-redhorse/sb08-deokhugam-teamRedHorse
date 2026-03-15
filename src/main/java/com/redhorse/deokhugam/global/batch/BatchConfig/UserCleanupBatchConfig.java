package com.redhorse.deokhugam.global.batch.BatchConfig;

import com.redhorse.deokhugam.global.batch.repository.UserBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
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

  // Step 설정
  @Bean
  public Step deleteOldUsersStep() {
    return new StepBuilder("deleteOldUsersStep", jobRepository)
        .tasklet(deleteOldUsersTasklet(), transactionManager)
        .listener(userCleanupStepListener()) // 리스너 등록
        .build();
  }

  // StepExecutionListener 설정
  @Bean
  public StepExecutionListener userCleanupStepListener() {
    return new StepExecutionListener() {
      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        // Step이 성공적으로 종료되었고, 실제로 삭제된 데이터가 있는 경우에만 캐시 무효화
        if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED) && 
            stepExecution.getWriteCount() > 0) {
          
          Cache cache = cacheManager.getCache("getUser");
          if (cache != null) {
            cache.clear();
            log.info("[User-Batch] 배치 종료 후 'getUser' 캐시 전체 무효화 완료. 삭제 건수: {}", 
                     stepExecution.getWriteCount());
          }
        }
        return stepExecution.getExitStatus();
      }
    };
  }

  // Tasklet 로직
  private Tasklet deleteOldUsersTasklet() {
    RetryTemplate retryTemplate = new RetryTemplateBuilder()
        .maxAttempts(3)
        .fixedBackoff(2000)
        .build();

    return (contribution, chunkContext) -> {
      int deletedRows = retryTemplate.execute(context ->
          userBatchRepository.deleteSoftDeletedUsersInBulk());

      // writeCount에 누적하여 나중에 listener에서 참조할 수 있게 함
      contribution.getStepExecution().setWriteCount(
          contribution.getStepExecution().getWriteCount() + deletedRows
      );

      return deletedRows > 0 ? RepeatStatus.CONTINUABLE : RepeatStatus.FINISHED;
    };
  }
}
