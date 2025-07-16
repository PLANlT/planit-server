package com.planit.planit.member.service;

import com.planit.planit.member.association.SignedMember;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import com.planit.planit.web.dto.member.term.TermDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    SignedMember getSignedMemberByUserInfo(String email, String name, SignType signType);

    SignedMember getSignedMemberById(Long id);

    MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId);

    void completeTermsAgreement(String signUpToken, TermDTO.AgreementRequest agreementRequest);

    MemberInfoResponseDTO getMemberInfo(Long memberId);
}
