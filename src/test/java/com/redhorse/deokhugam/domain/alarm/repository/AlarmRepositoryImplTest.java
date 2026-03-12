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

        // [핵심 변경 사항] Java 시간과 DB 시간의 오차를 없애기 위해 밀리초 단위로 절사(truncate)
        Instant nowTruncated = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReflectionTestUtils.setField(testUser, "createdAt", nowTruncated);
        ReflectionTestUtils.setField(testUser, "updatedAt", nowTruncated);
        em.persist(testUser);

        // 기준 시간도 밀리초 단위로 맞춰줍니다.
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

        // Repository 내에서 +1을 하므로, 정확히 3(limit)을 넘겨줍니다.
        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 4");
        assertThat(result.getContent().get(2).getMessage()).isEqualTo("메시지 2");
    }

    @Test
    @DisplayName("커서 기반 페이징 - 내림차순(최신순) 두 번째 페이지 조회 (커서 작동 확인)")
    void getAllAlarms_NextPage_Desc() {
        Alarm lastAlarmOfFirstPage = alarms.get(2);

        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(),
                "DESC",
                lastAlarmOfFirstPage.getId().toString(),
                lastAlarmOfFirstPage.getCreatedAt(),
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
        Alarm lastAlarmOfFirstPage = alarms.get(2);

        NotificationListRequest request = new NotificationListRequest(
                testUser.getId(),
                "ASC",
                lastAlarmOfFirstPage.getId().toString(),
                lastAlarmOfFirstPage.getCreatedAt(),
                3
        );

        Slice<Alarm> result = alarmRepository.getAllAlarms(request, PageRequest.of(0, 3));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("메시지 3");
        assertThat(result.getContent().get(1).getMessage()).isEqualTo("메시지 4");
    }
}