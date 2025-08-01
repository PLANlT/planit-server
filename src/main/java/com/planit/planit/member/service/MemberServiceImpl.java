package com.planit.planit.member.service;

import com.planit.planit.auth.jwt.JwtProvider;
import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.association.*;
import com.planit.planit.member.enums.GuiltyFreeReason;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.member.repository.GuiltyFreeRepository;
import com.planit.planit.member.association.Notification;
import com.planit.planit.member.repository.FcmTokenRepository;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.repository.NotificationRepository;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final GuiltyFreeRepository guiltyFreeRepository;
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final JwtProvider jwtProvider;


    @Override
    public SignedMember getSignedMemberByUserInfo(String email, String name, SignType signType) {
        // 활성 회원만 조회 (inactive가 null인 회원)
        Member member = memberRepository.findByEmailAndInactiveIsNull(email).orElse(null);
        
        // 사용자가 존재하지 않으면 신규 회원을 생성하여 반환
        if (member == null) {
            return SignedMember.of(saveMember(email, name, signType), true);
        }
        
        // 다른 로그인 타입으로 가입한 회원이면 예외 처리
        if (!member.getSignType().equals(signType)) {
            throw new MemberHandler(MemberErrorStatus.DIFFERENT_SIGN_TYPE);
        }
        
        return SignedMember.of(member, false);
    }

    @Override
    public SignedMember getSignedMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return SignedMember.of(member, false);
    }

    private Member saveMember(String email,  String name, SignType signType) {

        Member member = Member.builder()
                .email(email)
                .memberName(name)
                .password(UUID.randomUUID().toString().substring(0, 10))
                .signType(signType)
                .guiltyFreeMode(false)
                .dailyCondition(null)
                .role(Role.USER)
                .build();
        member = memberRepository.save(member);

        // 길티프리 설정 초기화
        saveGuiltyFree(member);

        // 알림 설정 저장
        saveNotification(member);

        return member;
    }

    private void saveNotification(Member member) {
        Notification notification = Notification.of(member);
        notificationRepository.save(notification);
    }

    private void saveGuiltyFree(Member member) {
        GuiltyFree guiltyFree = GuiltyFree.of(member, GuiltyFreeReason.NONE, GuiltyFreeProperty.guiltyFreeInitDate);
        guiltyFree = guiltyFreeRepository.save(guiltyFree);
        member.addGuiltyFree(guiltyFree);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        return MemberResponseDTO.ConsecutiveDaysDTO.of(member);
    }

    @Override
    @Transactional
    public LocalDateTime completeTermsAgreement(String signUpToken) {

        // 회원가입용 토큰 검증
        Long memberId = jwtProvider.validateSignUpTokenAndGetId(signUpToken);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));

        // 이미 약관 동의가 완료된 경우 예외 처리
        if (member.isSignUpCompleted()) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_ALREADY_SIGN_UP_COMPLETED);
        }

        // Term 저장
        LocalDateTime now = LocalDateTime.now();
        Term term = Term.builder()
                .member(member)
                .termOfUse(now)
                .termOfPrivacy(now)
                .termOfInfo(now)
                .thirdPartyAdConsent(now)
                .overFourteen(now)
                .build();
        termRepository.save(term);

        // isSignUpCompleted 업데이트
        member.completeSignUp();
        member.setTerm(term); // 양방향 매핑도 같이 갱신

        // 저장
        log.info("✅ 약관 동의 성공 - id: {}, email: {}", member.getId(), member.getEmail());
        return now;
    }

    @Override
    public MemberInfoResponseDTO getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        return MemberInfoResponseDTO.of(member);
    }

//    @Override                 수정할게 없어보이는데.. 해야하나?
//    @Transactional
//    public void updateMemberInfo(Long memberId, MemberInfoRequestDTO request) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
//        member.updateInfo(request.getName(), request.getEmail());
//    }

    /**
     * 회원 탈퇴(soft delete) - inactive에 현재 시간 기록
     */
    @Override
    public void inactivateMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
        if (member.getInactive() != null) {
            throw new MemberHandler(MemberErrorStatus.ALREADY_INACTIVE);
        }
        member.inactivate();
        memberRepository.save(member);
        log.info("[회원탈퇴] 탈퇴 처리 완료 - memberId: {}", memberId);
    }

    /**
     * 31일 경과한 탈퇴 회원 hard delete (스케줄러에서 호출)
     */
    @Override   
    public void deleteInactiveMembers() {
        log.info("[회원탈퇴] 31일 경과 회원 하드 딜리트 시작");
        LocalDateTime threshold = LocalDateTime.now().minusDays(31);
        memberRepository.findAll().stream()
                .filter(m -> m.getInactive() != null && m.getInactive().isBefore(threshold))
                .forEach(m -> {
                    memberRepository.deleteById(m.getId());
                    log.info("[회원탈퇴] 하드 딜리트 완료 - memberId: {}", m.getId());
                });
        log.info("[회원탈퇴] 31일 경과 회원 하드 딜리트 종료");
    }

}
