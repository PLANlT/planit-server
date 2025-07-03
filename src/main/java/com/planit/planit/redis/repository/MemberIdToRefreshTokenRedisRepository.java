package com.planit.planit.redis.repository;

import com.planit.planit.redis.entity.MemberIdToRefreshTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberIdToRefreshTokenRedisRepository extends CrudRepository<MemberIdToRefreshTokenRedisEntity, Long> {
} 