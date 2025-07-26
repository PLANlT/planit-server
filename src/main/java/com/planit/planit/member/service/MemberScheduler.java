package com.planit.planit.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 회원 관련 스케줄러
 * - 31일 경과한 탈퇴 회원 자동 삭제
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberScheduler {

    private final MemberService memberService;

    /**
     * 주석 처리된 스케줄러는 실제 운영에서 사용되는 스케줄러
     * 매일 새벽 2시에 31일 경과한 탈퇴 회원을 삭제
     * cron 표현식: 초 분 시 일 월 요일
     * 0 0 2 * * ? = 매일 새벽 2시 정각
     */
     @Scheduled(cron = "0 0 2 * * ?")
     public void deleteInactiveMembersScheduler() {
         log.info("[스케줄러] 31일 경과 탈퇴 회원 삭제 작업 시작");
         try {
             memberService.deleteInactiveMembers();
             log.info("[스케줄러] 31일 경과 탈퇴 회원 삭제 작업 완료");
         } catch (Exception e) {
             log.error("[스케줄러] 31일 경과 탈퇴 회원 삭제 작업 중 오류 발생: {}", e.getMessage(), e);
         }
     }

    /**
     * 개발/테스트용: 1분마다 실행 (실제 운영에서는 주석 처리)
     */
//    @Scheduled(fixedRate = 60000) // 60초 = 1분
//    public void deleteInactiveMembersSchedulerForTest() {
//        log.info("[스케줄러-테스트] 31일 경과 탈퇴 회원 삭제 작업 시작");
//        try {
//            memberService.deleteInactiveMembers();
//            log.info("[스케줄러-테스트] 31일 경과 탈퇴 회원 삭제 작업 완료");
//        } catch (Exception e) {
//            log.error("[스케줄러-테스트] 31일 경과 탈퇴 회원 삭제 작업 중 오류 발생: {}", e.getMessage(), e);
//        }
//    }
} 