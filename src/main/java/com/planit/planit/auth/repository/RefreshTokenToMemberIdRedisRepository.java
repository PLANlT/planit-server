package com.planit.planit.auth.repository;

import com.planit.planit.auth.entity.RefreshTokenToMemberIdRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenToMemberIdRedisRepository extends CrudRepository<RefreshTokenToMemberIdRedisEntity, String> {
} 