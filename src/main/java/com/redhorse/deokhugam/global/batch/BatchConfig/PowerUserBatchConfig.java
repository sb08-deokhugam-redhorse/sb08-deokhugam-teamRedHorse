package com.redhorse.deokhugam.global.batch.BatchConfig;


import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.UserBatchDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.dashboard.repository.PowerUserRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import com.redhorse.deokhugam.global.batch.repository.UserBatchRepository;
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
public class PowerUserBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final UserBatchRepository userBatchRepository;
    private final PowerUserRepository powerUserRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;
    private final AlarmMapper alarmMapper;

    @Bean
    public Job userRankingBatchJob() {
        return new JobBuilder("userRankingBatchJob", jobRepository)
                .start(userRankingBatchDailyStep())  // 1. 일간 실행
                .next(userRankingBatchWeeklyStep())  // 2. 주간 실행
                .next(userRankingBatchmonthlyStep())  // 3. 월간 실행
                .next(userRankingBatchAllStep())     // 4. 전체 기간 실행
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {

                        if (jobExecution.getStatus() == BatchStatus.FAILED) {
                            log.error("[PowerUser-batch] 에러 <배치 실행 중 에러 발생>: detail = {}",
                                    jobExecution.getAllFailureExceptions());

                        } else if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("[PowerUser-batch] 작업 완료: {}",
                                    jobExecution.getStepExecutions().iterator().next().getWriteCount());
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step userRankingBatchDailyStep() {
        return new StepBuilder("dailyStep", jobRepository)
                .<UserBatchDto, PowerUser>chunk(10000, transactionManager)
                .reader(userRepositoryDailyRead())
                .processor(userItemProcessor())
                .writer(userWriter())
                .build();
    }

    @Bean
    public Step userRankingBatchWeeklyStep() {
        return new StepBuilder("weeklyStep", jobRepository)
                .<UserBatchDto, PowerUser>chunk(10000, transactionManager)
                .reader(userRepositoryWeelyRead())
                .processor(userItemProcessor())
                .writer(userWriter())
                .build();
    }

    @Bean
    public Step userRankingBatchmonthlyStep() {
        return new StepBuilder("monthlyStep", jobRepository)
                .<UserBatchDto, PowerUser>chunk(10000, transactionManager)
                .reader(userRepositoryMonthlyRead())
                .processor(userItemProcessor())
                .writer(userWriter())
                .build();
    }

    @Bean
    public Step userRankingBatchAllStep() {
        return new StepBuilder("allTimeStep", jobRepository)
                .<UserBatchDto, PowerUser>chunk(10000, transactionManager)
                .reader(userRepositoryAllRead())
                .processor(userItemProcessor())
                .writer(userWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<UserBatchDto> userRepositoryWeelyRead() {
        return userRepositoryRead(WEEKLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<UserBatchDto> userRepositoryMonthlyRead() {
        return userRepositoryRead(MONTHLY);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<UserBatchDto> userRepositoryAllRead() {
        return userRepositoryRead(ALL_TIME);
    }

    @Bean
    @StepScope
    public RepositoryItemReader<UserBatchDto> userRepositoryDailyRead() {
        return userRepositoryRead(DAILY);
    }

    @Bean
    @StepScope
    public ItemProcessor<UserBatchDto, PowerUser> userItemProcessor() {
        return new ItemProcessor<UserBatchDto, PowerUser>() {
            private Long currentRank = 1L; // 현재 순위
            private Long processCount = 0L; // 지금까지 처리한 데이터 개수
            private Double previousScore = -1.0; // 직전 데이터의 점수 (동점자 처리용)

            @Override
            public PowerUser process(UserBatchDto item) throws Exception {
                // DB 조회 없이 식별자(ID)만 가진 프록시 객체 생성 (성능 대폭 향상)
                User user = userRepository.getReferenceById(item.userId());
                PeriodType periodType = PeriodType.valueOf(item.period());

                processCount++;
                if (previousScore != -1.0 && !previousScore.equals(item.score())) {
                    currentRank = processCount;
                }
                previousScore = item.score();

                return new PowerUser(
                        periodType,
                        currentRank,
                        item.score(),
                        item.reviewScoreSum(),
                        item.likeCount(),
                        item.commentCount(),
                        user
                );
            }
        };
    }

    @Bean
    public ItemWriter<PowerUser> userWriter() {
        RepositoryItemWriter<PowerUser> repositoryItemWriter =
                new RepositoryItemWriterBuilder<PowerUser>()
                        .repository(powerUserRepository)
                        .methodName("save")
                        .build();

        ItemWriter<PowerUser> serviceCallWriter = chunk ->
                chunk.getItems().stream()
                        .filter(item -> item.getRanking() <= 10)
                        .forEach(item -> {
                            try {
                                alarmService.createPowerUserAlarm(alarmMapper.toPowerUserDto(item));
                            } catch (Exception e) {
                                log.error("[Alarm-Service] 알람 생성 중 에러 발생. PowerUser ID: {}, 원인: {}",
                                        item.getId(), e.getMessage());
                            }
                        });

        return new CompositeItemWriterBuilder<PowerUser>()
                .delegates(repositoryItemWriter, serviceCallWriter)
                .build();
    }

    private RepositoryItemReader<UserBatchDto> userRepositoryRead(PeriodType period) {
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

        return new RepositoryItemReaderBuilder<UserBatchDto>()
                .name("userRepositoryRead_" + period.toString())
                .pageSize(1000)
                .methodName("findPowerUsers")
                .repository(userBatchRepository)
                .arguments(List.of(period.name(), startOfEnd, startOfToday))
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

    }

}

