package com.planit.planit.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberSchedulerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberScheduler memberScheduler;

    @Test
    @DisplayName("스케줄러가 정상적으로 deleteInactiveMembers를 호출한다")
    void testDeleteInactiveMembersScheduler() {
        // given
        doNothing().when(memberService).deleteInactiveMembers();

        // when
        memberScheduler.deleteInactiveMembersScheduler();

        // then
        verify(memberService, times(1)).deleteInactiveMembers();
    }

    @Test
    @DisplayName("스케줄러 실행 중 예외가 발생해도 로그만 남기고 정상 종료된다")
    void testDeleteInactiveMembersScheduler_withException() {
        // given
        doThrow(new RuntimeException("테스트 예외")).when(memberService).deleteInactiveMembers();

        // when & then
        // 예외가 발생해도 테스트가 실패하지 않아야 함
        memberScheduler.deleteInactiveMembersScheduler();
        
        verify(memberService, times(1)).deleteInactiveMembers();
    }
} 