package com.planit.planit.redis.repository;

import com.planit.planit.redis.entity.BlacklistTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRedisRepository extends CrudRepository<BlacklistTokenRedisEntity, String> {
} 