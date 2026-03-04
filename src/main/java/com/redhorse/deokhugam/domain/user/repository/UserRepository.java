package com.redhorse.deokhugam.domain.user.repository;

import com.redhorse.deokhugam.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Boolean existsUserByEmail(String email);
}
