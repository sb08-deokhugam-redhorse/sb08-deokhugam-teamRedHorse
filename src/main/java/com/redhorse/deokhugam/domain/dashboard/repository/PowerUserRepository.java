package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PowerUserRepository extends JpaRepository<PowerUser, UUID> {
}
