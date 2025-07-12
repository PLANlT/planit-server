package com.planit.planit.member.repository;

import com.planit.planit.member.association.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByTokenIn(List<String> invalidTokens);
}
