package com.planit.planit.web.dto.member;


import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoDTO {
    private Long id;
    private String email;
    private String name;
    private String signType;

    @Builder
    public MemberInfoDTO(Long id, String email, String name, SignType signType) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.signType = String.valueOf(signType);
    }

    public static MemberInfoDTO of(Member member) {
        return MemberInfoDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getMemberName())
                .signType(member.getSignType())
                .build();
    }

}
