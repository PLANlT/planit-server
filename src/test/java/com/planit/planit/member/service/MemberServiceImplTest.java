package com.planit.planit.member.service;

import com.planit.planit.auth.oauth.SocialTokenVerifier;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class MemberServiceImplTest {
    @MockBean
    private SocialTokenVerifier socialTokenVerifier;

    @InjectMocks
    private MemberServiceImpl memberServiceImpl;
}

