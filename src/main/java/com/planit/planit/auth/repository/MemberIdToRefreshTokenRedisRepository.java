package com.planit.planit.auth.repository;

import com.planit.planit.auth.entity.MemberIdToRefreshTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberIdToRefreshTokenRedisRepository extends CrudRepository<MemberIdToRefreshTokenRedisEntity, Long> {
} 