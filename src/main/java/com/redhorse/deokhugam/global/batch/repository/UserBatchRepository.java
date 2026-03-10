package com.redhorse.deokhugam.global.batch.repository;

import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.UserBatchDto;
import com.redhorse.deokhugam.domain.user.entity.User;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface UserBatchRepository extends JpaRepository<User, UUID> {

  // @Modifying(clearAutomatically = true) → nativeQuery사용시 영속성 컨텍스트 캐시를 거치지 않기때문에 모두 비워줘야함
  @Modifying(clearAutomatically = true)
  @Query(value = """
      DELETE FROM users 
      WHERE id IN (
        SELECT id 
        FROM users 
        WHERE is_deleted = TRUE 
        AND deleted_at < CURRENT_TIMESTAMP - INTERVAL '1 day'
        LIMIT 500
      )
      """, nativeQuery = true)

  int deleteSoftDeletedUsersInBulk();
    @Query("SELECT new com.redhorse.deokhugam.domain.dashboard.dto.poweruser.UserBatchDto(" +
            "CAST(:period AS string), " +
            "u.id, " +
            "SUM(r.likeCount), " +
            "SUM(r.commentCount), " +
            "CAST(SUM(r.rating) * 0.5 + SUM(r.commentCount) * 0.3 + SUM(r.likeCount) * 0.2 AS double), " +
            "CAST(SUM(r.rating) AS double) )" +
            "FROM User u " +
            "LEFT JOIN Review r ON r.user = u AND r.deletedAt IS NULL " +
            "WHERE u.deletedAt IS NULL " +
            "GROUP BY u.id")
    Page<UserBatchDto> findPowerUsers(
            @Param("period") String period,
            @Param("startDay") Instant startDay,
            @Param("endDay") Instant endDay,
            Pageable pageable
    );
}
