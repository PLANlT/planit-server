package com.planit.planit.member.service;

import com.planit.planit.web.dto.member.MemberInfoResponseDTO;
import com.planit.planit.web.dto.member.MemberResponseDTO;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    MemberResponseDTO.ConsecutiveDaysDTO getConsecutiveDays(Long memberId);

    void completeTermsAgreement(Long id, TermAgreementDTO.Request request);

    MemberInfoResponseDTO getMemberInfo(Long memberId);
}
