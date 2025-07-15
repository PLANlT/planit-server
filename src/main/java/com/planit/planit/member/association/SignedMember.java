package com.planit.planit.member.association;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 로그인 된 사용자의 정보를 조회하기 위한 DTO
 * sign-in : 소셜로그인이 완료된 후 액세스 토큰을 생성하기 전 사용자 정보를 담는 용도로 사용
 * refresh : 플랜잇에 로그인한 사용자 정보를 담는 용도로 사용
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SignedMember {
    private final Long id;
    private final String email;
    private final String name;
    private final Role role;
    private final Boolean isNewMember;
    private final Boolean isSignUpCompleted;

    public static SignedMember of(Member member, Boolean isNewMember) {
        return new SignedMember(member.getId(), member.getEmail(), member.getMemberName(),
                                member.getRole(), isNewMember, member.isSignUpCompleted());
    }
}
