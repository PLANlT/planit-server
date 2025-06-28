package com.planit.planit.config.oauth;

import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User delegate;
    @Getter
    private final SignType signType;
    @Getter
    private final Long id;
    @Getter
    private final String email;
    @Getter
    private final Role role;
    @Getter
    private final String memberName;

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

}
