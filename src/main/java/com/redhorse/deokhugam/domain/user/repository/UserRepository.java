package com.redhorse.deokhugam.domain.user.repository;

import com.redhorse.deokhugam.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
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
  Boolean existsUserByEmail(@Param("email") String email);

  Optional<User> findByEmail(String email);

  /**
   * SQLRestriction를 우회 사용자 검색 (ID로 검색)
   */
  @Query(value = """
      SELECT *
      FROM users u
      WHERE u.id = :id
      """, nativeQuery = true)
  Optional<User> findByIdIncludeDeleted(@Param("id") UUID id);


  /**
   * SQLRestriction 우회 물리 삭제
   */
  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = """
      DELETE FROM users u 
      WHERE u.id = :id
      """, nativeQuery = true)
  int deleteHardById(@Param("id") UUID id);
}
