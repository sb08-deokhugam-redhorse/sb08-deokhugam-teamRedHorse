package com.redhorse.deokhugam.domain.alarm.repository;

import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {

    List<Alarm> findAllAlarmByUserId(UUID userId);

    @Query("SELECT a FROM Alarm a " +
            "WHERE a.user.id = :#{#request.userId} " +
            "AND (" +
            "  (:#{#request.after == null ? true : false} = true) OR " +
            "  (a.createdAt < :#{#request.after}) OR " +
            "  (a.createdAt = :#{#request.after} AND a.id < :#{#request.cursor}) " +
            ")")
    Slice<Alarm> getAllAlarmsDesc(@Param("request") NotificationListRequest request, Pageable pageable);

    @Query("SELECT a FROM Alarm a " +
            "WHERE a.user.id = :#{#request.userId} " +
            "AND (" +
            "  (:#{#request.after == null ? true : false} = true) OR " +
            "  (a.createdAt > :#{#request.after}) " +
            "  OR (a.createdAt = :#{#request.after} AND a.id > :#{#request.cursor})" +
            ")")
    Slice<Alarm> getAllAlarmsAsc(@Param("request") NotificationListRequest request, Pageable pageable);
    Long countAlarmsByUserId(UUID userId);
}