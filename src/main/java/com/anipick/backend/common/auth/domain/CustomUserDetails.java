package com.anipick.backend.common.auth.domain;

import com.anipick.backend.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;
    private Map<String, Object> attributes;

    // 일반 로그인 사용자
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 소셜 로그인 사용자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public Long getUserId() {
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getProfileImageUrl() {
        return user.getProfileImageUrl();
    }

    public String getLoginFormat() {
        return user.getLoginFormat();
    }

    public Boolean getTermsAndConditions() {
        return user.getTermsAndConditions();
    }

    public Boolean getAdultYn() {
        return user.getAdultYn();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
