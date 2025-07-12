package com.planit.planit.auth.jwt;

import com.planit.planit.member.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {


    private final Long id;
    private final String email;
    private final String memberName;
    private final Role role;

    public UserPrincipal(Long id, String email, String name, Role role) {
        this.id = id;
        this.email = email;
        this.memberName = name;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> "ROLE_" + role);
    }

    @Override public String getUsername() { return memberName; } //
    @Override public String getPassword() { return null; } // OAuth 용,
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}