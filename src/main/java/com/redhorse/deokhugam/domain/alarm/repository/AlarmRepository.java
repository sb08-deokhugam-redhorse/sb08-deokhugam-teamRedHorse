package com.redhorse.deokhugam.domain.alarm.repository;

import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {

    List<Alarm> findAllAlarmByUserId(UUID userId);

    // 벌크 연산(INSERT, UPDATE, DELETE)에는 @Modifying이 필수입니다.
    @Modifying(clearAutomatically = true)
    @Query(value =
            "DELETE FROM alarms WHERE id IN (" +
                    "  SELECT id FROM alarms " +
                    "  WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '7 days' " +
                    "  ORDER BY created_at DESC "+
                    "  LIMIT :chunkSize" +
                    ")",
            nativeQuery = true)
    int deleteOldAlarmsInBulk(@Param("chunkSize") int chunkSize);
}
