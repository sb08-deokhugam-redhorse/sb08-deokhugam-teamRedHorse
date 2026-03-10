package com.redhorse.deokhugam.global.batch.BatchConfig;


import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.ReviewBatchDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularReviewRepository;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.global.batch.repository.ReviewBatchRepository;
import com.redhorse.deokhugam.global.entity.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static com.redhorse.deokhugam.global.entity.PeriodType.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PopularReviewBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ReviewBatchRepository reviewBatchRepository;
    private final PopularReviewRepository popularReviewRepository;
    private final AlarmService alarmService;

    @Bean
    public Job reviewRankingBatchJob() {
        return new JobBuilder("reviewRankingBatchJob", jobRepository)
                .start(reviewRankingBatchDailyStep())  // 1. 일간 실행
                .next(reviewRankingBatchWeeklyStep())  // 2. 주간 실행
                .next(reviewRankingBatchMonthlyStep())  // 3. 월간 실행
                .next(reviewRankingBatchAllStep())     // 4. 전체 기간 실행
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {

                        if (jobExecution.getStatus() == BatchStatus.FAILED) {
                            log.error("[Popularreview-batch] 에러 <배치 실행 중 에러 발생>: detail = {}",
                                    jobExecution.getAllFailureExceptions());

                        } else if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("[Popularreview-batch] 작업 완료: {}",
                                    jobExecution.getStepExecutions().iterator().next().getWriteCount());
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step reviewRankingBatchDailyStep() {
        return new StepBuilder("dailyStep", jobRepository)
                .<ReviewBatchDto, PopularReview>chunk(10000, transactionManager)
                .reader(reviewRepositoryDailyRead())
                .processor(reviewItemProcessor())
                .writer(reviewWriter())
                .build();
    }

    @Bean
    public Step reviewRankingBatchWeeklyStep() {
        return new StepBuilder("weeklyStep", jobRepository)
                .<ReviewBatchDto, PopularReview>chunk(10000, transactionManager)
                .reader(reviewRepositoryWeelyRead())
                .processor(reviewItemProcessor())
                .writer(reviewWriter())
                .build();
    }

    @Bean
    public Step reviewRankingBatchMonthlyStep() {
        return new StepBuilder("monthlyStep", jobRepository)
                .<ReviewBatchDto, PopularReview>chunk(10000, transactionManager)
                .reader(reviewRepositoryMonthlyRead())
                .processor(reviewItemProcessor())
                .writer(reviewWriter())
                .build();
    }

    @Bean
    public Step reviewRankingBatchAllStep() {
        return new StepBuilder("allTimeStep", jobRepository)
                .<ReviewBatchDto, PopularReview>chunk(10000, transactionManager)
                .reader(reviewRepositoryAllRead())
                .processor(reviewItemProcessor())
                .writer(reviewWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ReviewBatchDto> reviewRepositoryWeelyRead() {
        return reviewRepositoryRead(WEEKLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ReviewBatchDto> reviewRepositoryMonthlyRead() {
        return reviewRepositoryRead(MONTHLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ReviewBatchDto> reviewRepositoryAllRead() {
        return reviewRepositoryRead(ALL_TIME);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<ReviewBatchDto> reviewRepositoryDailyRead() {
        return reviewRepositoryRead(DAILY);
    }

    @Bean
    @StepScope
    public ItemProcessor<ReviewBatchDto, PopularReview> reviewItemProcessor() {
        return new ItemProcessor<ReviewBatchDto, PopularReview>() {
            private Long currentRank = 1L; // 현재 순위
            private Long processCount = 0L; // 지금까지 처리한 데이터 개수
            private Double previousScore = -1.0; // 직전 데이터의 점수 (동점자 처리용)

            @Override
            public PopularReview process(ReviewBatchDto item) throws Exception {
                // DB 조회 없이 식별자(ID)만 가진 프록시 객체 생성 (성능 대폭 향상)
                Review review = reviewBatchRepository.getReferenceById(item.reviewId());
                PeriodType periodType = PeriodType.valueOf(item.period());

                processCount++;
                if (previousScore != -1.0 && !previousScore.equals(item.score())) {
                    currentRank = processCount;
                }
                previousScore = item.score();

                return new PopularReview(
                        periodType,
                        currentRank,
                        item.score(),
                        item.likeCount(),
                        item.commentCount(),
                        review
                );
            }
        };
    }

    @Bean
    public ItemWriter<PopularReview> reviewWriter() {

        RepositoryItemWriter<PopularReview> repositoryItemWriter =
                new RepositoryItemWriterBuilder<PopularReview>()
                        .repository(popularReviewRepository)
                        .methodName("save")
                        .build();

        ItemWriter<PopularReview> serviceCallWriter = chunk ->
                chunk.getItems().stream()
                        .filter(item -> item.getRanking() <= 10)
                        .forEach(item -> alarmService.createReviewAlarm(item));

        return new CompositeItemWriterBuilder<PopularReview>()
                .delegates(repositoryItemWriter, serviceCallWriter)
                .build();
    }

    private RepositoryItemReader<ReviewBatchDto> reviewRepositoryRead(PeriodType period) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        Instant startOfToday = now.truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant startOfEnd = null;

        switch (period) {
            case DAILY -> {
                startOfEnd = now.minusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant();
            }
            case WEEKLY -> {
                startOfEnd = now.minusDays(7).truncatedTo(ChronoUnit.DAYS).toInstant();
            }
            case MONTHLY -> {
                startOfEnd = now.minusDays(30).truncatedTo(ChronoUnit.DAYS).toInstant();
            }
            case ALL_TIME -> {
                startOfEnd = Instant.EPOCH;
            }
        }

        return new RepositoryItemReaderBuilder<ReviewBatchDto>()
                .name("reviewRepositoryRead_" + period.toString())
                .pageSize(1000)
                .methodName("findReviews")
                .repository(reviewBatchRepository)
                .arguments(List.of(period.name(), startOfEnd, startOfToday)) // ★ 추가: 레포지토리에 넘길 파라미터 세팅
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

    }

}

