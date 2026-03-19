package com.redhorse.deokhugam.global.batch.BatchConfig;

import com.redhorse.deokhugam.domain.book.entity.Book;
import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.BookBatchDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularBookRepository;
import com.redhorse.deokhugam.domain.dashboard.service.DashboardService;
import com.redhorse.deokhugam.global.batch.repository.BookBatchRepository;
import com.redhorse.deokhugam.global.entity.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
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
public class PopularBookBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final BookBatchRepository bookBatchRepository;
    private final PopularBookRepository popularBookRepository;
    private final DashboardService dashboardService;


    @Bean
    public Job bookRankingBatchJob() {
        return new JobBuilder("bookRankingBatchJob", jobRepository)
                .start(bookRankingBatchDailyStep())  // 1. 일간 실행
                .next(bookRankingBatchWeeklyStep())  // 2. 주간 실행
                .next(bookRankingBatchMonthlyStep())  // 3. 월간 실행
                .next(bookRankingBatchAllStep())     // 4. 전체 기간 실행
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        if (jobExecution.getStatus() == BatchStatus.FAILED) {
                            log.error("[PopularBook-batch] 에러 <배치 실행 중 에러 발생>: detail = {}",
                                    jobExecution.getAllFailureExceptions());

                        } else if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("[PopularBook-batch] 작업 완료: {}",
                                    jobExecution.getStepExecutions().iterator().next().getWriteCount());
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step bookRankingBatchDailyStep() {
        return new StepBuilder("dailyStep", jobRepository)
                .<BookBatchDto, PopularBook>chunk(500, transactionManager)
                .reader(bookRepositoryDailyRead())
                .processor(bookItemProcessor())
                .writer(bookWriter())
                .faultTolerant() // 내결함성 기능 활성화
                .processorNonTransactional()
                .retryLimit(3)   // 최대 3번 재시도
                .retry(org.springframework.dao.TransientDataAccessException.class)
                .noRetry(com.redhorse.deokhugam.domain.book.exception.BookException.class)
                .build();
    }

    @Bean
    public Step bookRankingBatchWeeklyStep() {
        return new StepBuilder("weeklyStep", jobRepository)
                .<BookBatchDto, PopularBook>chunk(500, transactionManager)
                .reader(bookRepositoryWeeklyRead())
                .processor(bookItemProcessor())
                .writer(bookWriter())
                .faultTolerant() // 내결함성 기능 활성화
                .processorNonTransactional()
                .retryLimit(3)   // 최대 3번 재시도
                .retry(org.springframework.dao.TransientDataAccessException.class)
                .noRetry(com.redhorse.deokhugam.domain.book.exception.BookException.class)
                .build();
    }

    @Bean
    public Step bookRankingBatchMonthlyStep() {
        return new StepBuilder("monthlyStep", jobRepository)
                .<BookBatchDto, PopularBook>chunk(500, transactionManager)
                .reader(bookRepositoryMonthlyRead())
                .processor(bookItemProcessor())
                .writer(bookWriter())
                .faultTolerant() // 내결함성 기능 활성화
                .processorNonTransactional()
                .retryLimit(3)   // 최대 3번 재시도
                .retry(org.springframework.dao.TransientDataAccessException.class)
                .noRetry(com.redhorse.deokhugam.domain.book.exception.BookException.class)
                .build();
    }

    @Bean
    public Step bookRankingBatchAllStep() {
        return new StepBuilder("allTimeStep", jobRepository)
                .<BookBatchDto, PopularBook>chunk(500, transactionManager)
                .reader(bookRepositoryAllRead())
                .processor(bookItemProcessor())
                .writer(bookWriter())
                .faultTolerant() // 내결함성 기능 활성화
                .processorNonTransactional()
                .retryLimit(3)   // 최대 3번 재시도
                .retry(org.springframework.dao.TransientDataAccessException.class)
                .noRetry(com.redhorse.deokhugam.domain.book.exception.BookException.class)
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<BookBatchDto> bookRepositoryWeeklyRead() {
        return bookRepositoryRead(WEEKLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<BookBatchDto> bookRepositoryMonthlyRead() {
        return bookRepositoryRead(MONTHLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<BookBatchDto> bookRepositoryAllRead() {
        return bookRepositoryRead(ALL_TIME);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<BookBatchDto> bookRepositoryDailyRead() {
        return bookRepositoryRead(DAILY);
    }

    @Bean
    @StepScope
    public ItemProcessor<BookBatchDto, PopularBook> bookItemProcessor() {
        return new ItemProcessor<BookBatchDto, PopularBook>() {
            private Long currentRank = 1L; // 현재 순위
            private Long processCount = 0L; // 지금까지 처리한 데이터 개수
            private Double previousScore = -1.0; // 직전 데이터의 점수 (동점자 처리용)

            @Override
            public PopularBook process(BookBatchDto item) throws Exception {
                // DB 조회 없이 식별자(ID)만 가진 프록시 객체 생성 (성능 대폭 향상)
                Book book = bookBatchRepository.getReferenceById(item.bookId());
                PeriodType periodType = PeriodType.valueOf(item.period());

                processCount++;
                if (previousScore != -1.0 && !previousScore.equals(item.score())) {
                    currentRank = processCount;
                }
                previousScore = item.score();

                return new PopularBook(
                        periodType,
                        item.rating(),
                        item.score(),
                        item.reviewCount(),
                        currentRank,
                        book
                );
            }
        };
    }

    @Bean
    public RepositoryItemWriter<PopularBook> bookWriter() {
        return new RepositoryItemWriterBuilder<PopularBook>()
                .repository(popularBookRepository)
                .methodName("save")
                .build();
    }

    private RepositoryItemReader<BookBatchDto> bookRepositoryRead(PeriodType period) {
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

        return new RepositoryItemReaderBuilder<BookBatchDto>()
                .name("BookRepositoryRead_" + period.toString())
                .pageSize(500)
                .methodName("findBooks")
                .repository(bookBatchRepository)
                .arguments(List.of(period.name(), startOfEnd, startOfToday)) // ★ 추가: 레포지토리에 넘길 파라미터 세팅
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }
}