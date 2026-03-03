package com.redhorse.deokhugam.domain.alarm.repository;

import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlarmRepository extends JpaRepository<Alarm, UUID> {
}
