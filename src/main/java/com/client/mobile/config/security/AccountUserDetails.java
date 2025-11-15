package com.client.mobile.config.security;

import com.client.mobile.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    private AccountUserDetails(String username, String password, boolean active,
                               Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.active = active;
        this.authorities = authorities;
    }

    public static AccountUserDetails build(Account account) {
        Set<GrantedAuthority> authorities = account.getRoles().stream()
                .flatMap(role -> {

                    Set<GrantedAuthority> roleAuthorities = role.getPermissions().stream()
                            .map(perm -> new SimpleGrantedAuthority(perm.getPermissionName()))
                            .collect(Collectors.toSet());
                    roleAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
                    return roleAuthorities.stream();
                })
                .collect(Collectors.toSet());

        boolean isActive = "active".equalsIgnoreCase(account.getStatus());

        return new AccountUserDetails(
                account.getFullName(),
                account.getPassword(),
                isActive,
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }
}
