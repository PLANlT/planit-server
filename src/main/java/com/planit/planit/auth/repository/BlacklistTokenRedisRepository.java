package com.planit.planit.auth.repository;

import com.planit.planit.auth.entity.BlacklistTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRedisRepository extends CrudRepository<BlacklistTokenRedisEntity, String> {
} 