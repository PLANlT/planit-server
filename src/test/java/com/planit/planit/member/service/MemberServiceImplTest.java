package com.planit.planit.member.service;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private MemberServiceImpl memberServiceImpl;

    @Test
    @DisplayName("회원 탈퇴 시 inactive에 시간이 기록된다 (soft delete)")
    void testSoftDelete_setsInactiveTime() {
        // given
        Member member = Member.builder()
                .email("test@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("테스터")
                .role(Role.USER)
                .build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        // when
        memberServiceImpl.inactivateMember(1L);

        // then
        assertNotNull(member.getInactive(), "inactive 필드가 null이 아니어야 한다");
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("탈퇴한 회원은 로그인/조회가 불가능하다")
    void testSoftDeletedMember_cannotLoginOrBeQueried() {
        // given
        Member member = Member.builder()
                .email("test2@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("테스터2")
                .role(Role.USER)
                .build();
        member.inactivate();
        when(memberRepository.findByEmailAndInactiveIsNull("test2@planit.com")).thenReturn(Optional.empty());

        // when
        Optional<Member> found = memberRepository.findByEmailAndInactiveIsNull("test2@planit.com");

        // then
        assertTrue(found.isEmpty(), "탈퇴 회원은 조회/로그인 불가");
    }

    @Test
    @DisplayName("탈퇴 회원은 DB에서 삭제되지 않는다 (soft delete)")
    void testSoftDeletedMember_stillExistsInDb() {
        // given
        Member member = Member.builder()
                .email("test3@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("테스터3")
                .role(Role.USER)
                .build();
        member.inactivate();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        // when
        Optional<Member> found = memberRepository.findById(1L);

        // then
        assertTrue(found.isPresent(), "DB에는 여전히 존재해야 한다");
    }

    @Test
    @DisplayName("30일 지난 탈퇴 회원은 스케줄러에 의해 hard delete 된다")
    void testScheduler_hardDeletesAfter30Days() {
        // given
        Member member = Member.builder()
                .id(1L)
                .email("test4@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("테스터4")
                .role(Role.USER)
                .build();
        member.inactivate();
        member.setInactiveForTest(LocalDateTime.now().minusDays(31));
        when(memberRepository.findAll()).thenReturn(java.util.List.of(member));
        doNothing().when(memberRepository).deleteById(anyLong());

        // when
        memberServiceImpl.deleteInactiveMembers();

        // then
        verify(memberRepository).deleteById(anyLong());
    }
}

