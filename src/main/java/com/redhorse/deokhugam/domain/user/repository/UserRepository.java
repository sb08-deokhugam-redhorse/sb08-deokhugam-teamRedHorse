package com.redhorse.deokhugam.domain.user.repository;

import com.redhorse.deokhugam.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * SQLRestriction("is_deleted = false") 를 우회하여
   * 이메일 중복 검사를 하기 위해 nativeQuery 사용
   */
  @Query(value = """
      SELECT COUNT(u.id) > 0
      FROM  users u
      WHERE u.email = :email
      """, nativeQuery = true)
  Boolean existsUserByEmail(String email);
}
