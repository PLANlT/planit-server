package com.planit.planit.member.service;

public interface FcmTokenService {
    void saveOrUpdateFcmToken(Long memberId, String token);
}
