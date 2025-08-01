package com.planit.planit.web.controller;

import com.planit.planit.auth.service.BlacklistTokenRedisService;
import com.planit.planit.auth.service.RefreshTokenRedisService;
import com.planit.planit.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/redis")
@Tag(name = "Redis Monitor", description = "Redis 데이터 모니터링 API")
public class RedisMonitorController {
    
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final BlacklistTokenRedisService blacklistTokenRedisService;
    
    public RedisMonitorController(RefreshTokenRedisService refreshTokenRedisService,
                                BlacklistTokenRedisService blacklistTokenRedisService) {
        this.refreshTokenRedisService = refreshTokenRedisService;
        this.blacklistTokenRedisService = blacklistTokenRedisService;
    }
    
    @GetMapping("/status")
    @Operation(summary = "Redis 상태 확인", description = "Redis에 저장된 토큰 개수와 상태를 확인합니다.")
    public ApiResponse<Map<String, Object>> getRedisStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            long refreshTokenCount = refreshTokenRedisService.getRefreshTokenCount();
            long blacklistedTokenCount = blacklistTokenRedisService.getBlacklistedTokenCount();
            
            status.put("refreshTokenCount", refreshTokenCount);
            status.put("blacklistedTokenCount", blacklistedTokenCount);
            status.put("totalTokenCount", refreshTokenCount + blacklistedTokenCount);
            status.put("status", "healthy");
            
            return ApiResponse.success(status);
        } catch (Exception e) {
            status.put("status", "error");
            status.put("error", e.getMessage());
            return ApiResponse.error(status);
        }
    }
    
    @PostMapping("/clear/refresh-tokens")
    @Operation(summary = "Refresh 토큰 전체 삭제", description = "Redis에 저장된 모든 refresh 토큰을 삭제합니다.")
    public ApiResponse<String> clearAllRefreshTokens() {
        try {
            refreshTokenRedisService.clearAllRefreshTokens();
            return ApiResponse.success("All refresh tokens cleared successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to clear refresh tokens: " + e.getMessage());
        }
    }
    
    @PostMapping("/clear/blacklisted-tokens")
    @Operation(summary = "Blacklist 토큰 전체 삭제", description = "Redis에 저장된 모든 blacklist 토큰을 삭제합니다.")
    public ApiResponse<String> clearAllBlacklistedTokens() {
        try {
            blacklistTokenRedisService.clearAllBlacklistedTokens();
            return ApiResponse.success("All blacklisted tokens cleared successfully");
        } catch (Exception e) {
            return ApiResponse.error("Failed to clear blacklisted tokens: " + e.getMessage());
        }
    }
    
    @PostMapping("/validate")
    @Operation(summary = "토큰 매핑 검증", description = "특정 memberId와 refreshToken의 매핑이 올바른지 검증합니다.")
    public ApiResponse<Boolean> validateTokenMapping(@RequestParam Long memberId, 
                                                   @RequestParam String refreshToken) {
        try {
            boolean isValid = refreshTokenRedisService.validateRefreshTokenMapping(memberId, refreshToken);
            return ApiResponse.success(isValid);
        } catch (Exception e) {
            return ApiResponse.error("Failed to validate token mapping: " + e.getMessage());
        }
    }
    
    @GetMapping("/debug/keys")
    @Operation(summary = "Redis 키 디버깅", description = "Redis에 저장된 모든 키와 타입을 확인합니다.")
    public ApiResponse<Map<String, Object>> debugRedisKeys() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        try {
            // Redis 키들을 확인하는 로직을 추가할 수 있습니다
            debugInfo.put("message", "Redis 키 디버깅 정보");
            debugInfo.put("note", "Redis CLI에서 'keys *' 명령어로 확인하세요");
            debugInfo.put("expectedType", "hash");
            debugInfo.put("currentIssue", "SET으로 저장되는 문제 해결 필요");
            
            return ApiResponse.success(debugInfo);
        } catch (Exception e) {
            debugInfo.put("error", e.getMessage());
            return ApiResponse.error(debugInfo);
        }
    }
} 