package com.planit.planit.member.service;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.Member;
import com.planit.planit.member.repository.MemberRepository;
import com.planit.planit.member.association.Term;
import com.planit.planit.member.repository.TermRepository;
import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;


    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));
        return MemberResponseDTO.ConsecutiveDaysDTO.of(member);
    }

    @Transactional
    public void completeTermsAgreement(Long memberId, TermAgreementDTO.Request request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorStatus.MEMBER_NOT_FOUND));

        // Term 저장
        Term term = Term.builder()
                .member(member)
                .termOfUse(request.getTermOfUse())
                .termOfPrivacy(request.getTermOfPrivacy())
                .termOfInfo(request.getTermOfInfo())
                .overFourteen(request.getOverFourteen())
                .build();
        termRepository.save(term);

        // isSignUpCompleted 업데이트
        member.setSignUpCompleted(true);
        member.setTerm(term); // 양방향 매핑도 같이 갱신

        // 저장
        memberRepository.save(member);
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

}
