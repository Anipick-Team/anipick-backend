package com.anipick.backend.common.auth.dto;

import com.anipick.backend.admin.domain.Admin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomAdminDetails implements UserDetails {
    private final Admin admin;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public Long getAdminId() {
        return admin.getAdminId();
    }

    @Override
    public String getUsername() {
        return admin.getUsername();
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return admin.getIsActive();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
