package com.planit.planit.redis.repository;

import com.planit.planit.redis.entity.RefreshTokenToMemberIdRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenToMemberIdRedisRepository extends CrudRepository<RefreshTokenToMemberIdRedisEntity, String> {
} 