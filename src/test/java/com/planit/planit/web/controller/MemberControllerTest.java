package com.planit.planit.web.controller;

import com.planit.planit.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 탈퇴 성공 - PATCH /members/delete")
    @WithMockUser(username = "1") // 인증된 사용자 시뮬레이션, principal.getId()가 1L이라고 가정
    void deleteMember_success() throws Exception {
        // given: memberService mock 세팅 (필요시)
        Mockito.doNothing().when(memberService).inactivateMember(Mockito.anyLong());

        // when & then
        mockMvc.perform(patch("/members/delete"))
                .andExpect(status().isOk());
    }

    // 추가 테스트: 인증 없는 요청, 이미 탈퇴한 회원 등은 추후 확장 가능
}
