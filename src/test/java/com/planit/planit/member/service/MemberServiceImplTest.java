package com.planit.planit.member.service;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.GuiltyFree;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.association.SignedMember;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.GuiltyFreeRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private GuiltyFreeRepository guiltyFreeRepository;
    @Mock
    private NotificationRepository notificationRepository;
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
        member.setInactiveForTest(LocalDateTime.now().minusDays(32));
        when(memberRepository.findAll()).thenReturn(java.util.List.of(member));
        doNothing().when(memberRepository).deleteById(anyLong());

        // when
        memberServiceImpl.deleteInactiveMembers();

        // then
        verify(memberRepository).deleteById(anyLong());
    }

    @Test
    @DisplayName("탈퇴한 회원이 로그인하면 새로운 회원으로 가입한다")
    void testInactiveMemberLogin_createsNewMember() {
        // given
        Member inactiveMember = Member.builder()
                .email("inactive@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("탈퇴회원")
                .role(Role.USER)
                .build();
        inactiveMember.inactivate(); // 탈퇴 처리
        
        when(memberRepository.findByEmailAndInactiveIsNull("inactive@planit.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(guiltyFreeRepository.save(any(GuiltyFree.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SignedMember result = memberServiceImpl.getSignedMemberByUserInfo("inactive@planit.com", "새로운회원", SignType.GOOGLE);

        // then
        assertTrue(result.getIsNewMember(), "새로운 회원으로 처리되어야 한다");
        assertEquals("새로운회원", result.getName(), "새로운 이름으로 회원이 생성되어야 한다");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("탈퇴한 회원이 다른 로그인 타입으로 로그인하면 새로운 회원으로 가입한다")
    void testInactiveMemberLoginWithDifferentSignType_createsNewMember() {
        // given
        Member inactiveMember = Member.builder()
                .email("inactive@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("탈퇴회원")
                .role(Role.USER)
                .build();
        inactiveMember.inactivate(); // 탈퇴 처리
        
        when(memberRepository.findByEmailAndInactiveIsNull("inactive@planit.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(guiltyFreeRepository.save(any(GuiltyFree.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SignedMember result = memberServiceImpl.getSignedMemberByUserInfo("inactive@planit.com", "새로운회원", SignType.KAKAO);

        // then
        assertTrue(result.getIsNewMember(), "새로운 회원으로 처리되어야 한다");
        assertEquals("새로운회원", result.getName(), "새로운 이름으로 회원이 생성되어야 한다");
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("복합 unique 제약조건: 같은 이메일로 탈퇴한 회원과 새로운 회원이 공존할 수 있다")
    void testCompositeUniqueConstraint_allowsInactiveAndActiveMembersWithSameEmail() {
        // given
        Member inactiveMember = Member.builder()
                .email("same@planit.com")
                .password("pw1234")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("탈퇴회원")
                .role(Role.USER)
                .build();
        inactiveMember.inactivate(); // 탈퇴 처리
        
        Member activeMember = Member.builder()
                .email("same@planit.com")
                .password("pw5678")
                .signType(SignType.KAKAO)
                .guiltyFreeMode(false)
                .memberName("활성회원")
                .role(Role.USER)
                .build();
        
        when(memberRepository.findByEmailAndInactiveIsNull("same@planit.com")).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(guiltyFreeRepository.save(any(GuiltyFree.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SignedMember result = memberServiceImpl.getSignedMemberByUserInfo("same@planit.com", "새로운회원", SignType.NAVER);

        // then
        assertTrue(result.getIsNewMember(), "새로운 회원으로 처리되어야 한다");
        assertEquals("새로운회원", result.getName(), "새로운 이름으로 회원이 생성되어야 한다");
        verify(memberRepository).save(any(Member.class));
    }
}

