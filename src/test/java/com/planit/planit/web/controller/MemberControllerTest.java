package com.planit.planit.web.controller;

import com.planit.planit.auth.jwt.UserPrincipal;
import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "oauth.google.client-id=dummy",
        "oauth.google.playground-client-id=dummy"
})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal(1L, "test@planit.com", "푸바오", Role.USER);
    }

    private UsernamePasswordAuthenticationToken auth(UserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteMember_success() throws Exception {
        UserPrincipal principal = mockPrincipal();
        doNothing().when(memberService).inactivateMember(principal.getId());

        mockMvc.perform(patch("/planit/members/delete")
                        .with(csrf())
                        .with(authentication(auth(principal))))
                .andExpect(status().isOk());

        verify(memberService).inactivateMember(principal.getId());
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 403 반환")
    void deleteMember_unauthenticated() throws Exception {
        mockMvc.perform(patch("/planit/members/delete").with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("이미 탈퇴한 회원은 예외 발생")
    void deleteMember_alreadyInactive() throws Exception {
        UserPrincipal principal = mockPrincipal();
        doThrow(new MemberHandler(MemberErrorStatus.ALREADY_INACTIVE))
                .when(memberService).inactivateMember(principal.getId());

        mockMvc.perform(patch("/planit/members/delete")
                        .with(csrf())
                        .with(authentication(auth(principal))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MEMBER4004"));

        verify(memberService).inactivateMember(principal.getId());
    }

    @Test
    @DisplayName("존재하지 않는 회원은 예외 발생")
    void deleteMember_memberNotFound() throws Exception {
        UserPrincipal principal = mockPrincipal();
        doThrow(new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND))
                .when(memberService).inactivateMember(principal.getId());

        mockMvc.perform(patch("/planit/members/delete")
                        .with(csrf())
                        .with(authentication(auth(principal))))
                .andExpect(status().isNotFound());

        verify(memberService).inactivateMember(principal.getId());
    }
}
