package com.redhorse.deokhugam.domain.alarm.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(AlarmRepositoryImplTest.QueryDslTestConfig.class)
class AlarmRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private JPAQueryFactory queryFactory;

    private AlarmRepositoryImpl alarmRepository;

    private User testUser;
    private final List<Alarm> alarms = new ArrayList<>();

    @TestConfiguration
    static class QueryDslTestConfig {
        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager em) {
            return new JPAQueryFactory(em);
        }
    }

    @BeforeEach
    void setUp() {
        alarmRepository = new AlarmRepositoryImpl(queryFactory);

        testUser = new User("test@email.com", "테스트유저", "password123");

        // [핵심 변경 1] CI DB 환경의 Timestamp 소수점 반올림 이슈를 방지하기 위해 초(SECONDS) 단위로 절사
        Instant nowTruncated = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ReflectionTestUtils.setField(testUser, "createdAt", nowTruncated);
        ReflectionTestUtils.setField(testUser, "updatedAt", nowTruncated);
        em.persist(testUser);

        Instant baseTime = nowTruncated.minusSeconds(100);
        UUID dummyReviewId = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            Alarm alarm = new Alarm(
                    "COMMENT",
                    "메시지 " + i,
                    "리뷰 내용입니다",
                    dummyReviewId,
                    testUser
            );

            Instant time = baseTime.plusSeconds(i * 10);
            ReflectionTestUtils.setField(alarm, "createdAt", time);
            ReflectionTestUtils.setField(alarm, "updatedAt", time);

            em.persist(alarm);
            alarms.add(alarm);
        }

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("커서 기반 페이징 - 내림차순(최신순) 첫 페이지 조회")
    void getAllAlarms_FirstPage_Desc() {
        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(), "DESC", null, null, 3
        );

        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 4");
        assertThat(result.getContent().get(2).getMessage()).isEqualTo("메시지 2");
    }

    @Test
    @DisplayName("커서 기반 페이징 - 내림차순(최신순) 두 번째 페이지 조회 (커서 작동 확인)")
    void getAllAlarms_NextPage_Desc() {
        // [핵심 변경 2] 실제 DB에서 첫 페이지를 먼저 조회하여 '확실한' 커서 데이터를 추출합니다.
        NotificationListRequest firstPageRequest = new NotificationListRequest(
                testUser.getId(), "DESC", null, null, 3
        );
        Slice<Alarm> firstPage = alarmRepository.getAllAlarms(firstPageRequest, PageRequest.of(0, 3));
        Alarm actualLastAlarm = firstPage.getContent().get(2); // 3개 중 마지막 데이터

        // 추출한 실제 데이터를 기준으로 두 번째 페이지를 요청합니다.
        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(),
                "DESC",
                actualLastAlarm.getId().toString(),
                actualLastAlarm.getCreatedAt(),
                3
        );

        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 1");
        assertThat(result.getContent().get(1).getMessage()).isEqualTo("메시지 0");
    }

    @Test
    @DisplayName("커서 기반 페이징 - 오름차순(과거순) 첫 페이지 조회")
    void getAllAlarms_FirstPage_Asc() {
        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(), "ASC", null, null, 3
        );

        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 0");
        assertThat(result.getContent().get(2).getMessage()).isEqualTo("메시지 2");
    }

    @Test
    @DisplayName("커서 기반 페이징 - 오름차순(과거순) 두 번째 페이지 조회 (커서 작동 확인)")
    void getAllAlarms_NextPage_Asc() {
        // [핵심 변경 2] 실제 DB에서 첫 페이지를 조회하여 커서 추출
        NotificationListRequest firstPageRequest = new NotificationListRequest(
                testUser.getId(), "ASC", null, null, 3
        );
        Slice<Alarm> firstPage = alarmRepository.getAllAlarms(firstPageRequest, PageRequest.of(0, 3));
        Alarm actualLastAlarm = firstPage.getContent().get(2);

        // 추출한 커서 데이터로 다음 페이지 요청
        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(),
                "ASC",
                actualLastAlarm.getId().toString(),
                actualLastAlarm.getCreatedAt(),
                3
        );

        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 3");
        assertThat(result.getContent().get(1).getMessage()).isEqualTo("메시지 4");
    }
}