package com.planit.planit.member.service;

import com.planit.planit.member.association.SignedMember;
import com.planit.planit.member.enums.SignType;
import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface MemberService {

    SignedMember getSignedMemberByUserInfo(String email, String name, SignType signType);

    SignedMember getSignedMemberById(Long id);

    MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId);

    LocalDateTime completeTermsAgreement(String signUpToken);

    MemberInfoResponseDTO getMemberInfo(Long memberId);

    void inactivateMember(Long memberId);

    void deleteInactiveMembers();
}
