package com.redhorse.deokhugam.global.batch.repository;

import com.redhorse.deokhugam.domain.user.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserBatchRepository extends JpaRepository<User, UUID> {

  // @Modifying(clearAutomatically = true) → nativeQuery사용시 영속성 컨텍스트 캐시를 거치지 않기때문에 모두 비워줘야함
  @Modifying(clearAutomatically = true)
  @Query(value = """
      DELETE FROM users 
      WHERE is_deleted = TRUE 
      AND deleted_at < CURRENT_TIMESTAMP - INTERVAL '1 day'
      """, nativeQuery = true)
  int deleteSoftDeletedUsersInBulk();
}
