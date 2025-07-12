package com.planit.planit.member.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface FcmTokenService {
    void saveOrUpdateFcmToken(Long memberId, String token);
    void deleteToken(String token);
    void deleteTokensByMemberId(Long memberId);
    void cleanUpInvalidTokens(List<String> invalidTokens); // optional
    Optional<String> getTokenByMemberId(Long memberId);

}
