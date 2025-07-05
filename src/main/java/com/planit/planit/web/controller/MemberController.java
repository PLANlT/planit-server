package com.planit.planit.web.controller;


import com.planit.planit.config.jwt.UserPrincipal;
import com.planit.planit.config.oauth.CustomOAuth2UserService;
import com.planit.planit.member.service.MemberService;
import com.planit.planit.web.dto.auth.login.OAuthLoginDTO;
import com.planit.planit.config.oauth.CustomOAuth2User;
import com.planit.planit.web.dto.member.term.TermAgreementDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;


    @PostMapping("/sign-in")
    public ResponseEntity<OAuthLoginDTO.Response> signIn(
            @RequestAttribute(name = "oauthUser") CustomOAuth2User oAuth2User,
            @RequestBody(required = false) TermAgreementDTO.Request termRequest
    ) {
        OAuthLoginDTO.Response response = memberService.signIn(oAuth2User, termRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(@AuthenticationPrincipal UserPrincipal principal, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring("Bearer ".length());
            memberService.signOut(principal.getId(), accessToken);
        }
        return ResponseEntity.ok().build();
    }
}
