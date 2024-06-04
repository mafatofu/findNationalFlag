package com.example.selfduizoo.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
public class CustomMemberDetails implements UserDetails {
    private Long id;
    private String userName;
    private String password;
    @Getter
    private String email;
    private Authority authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authority != null){
            String role = authority.getAuthority();
            return Collections.singleton(new SimpleGrantedAuthority(role));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
