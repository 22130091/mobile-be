package com.client.mobile.config.security;

import com.client.mobile.repository.AccountRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Service;

@Service("accountSecurityService")
public class AccountSecurityService {

    private final AccountRepository repo;

    public AccountSecurityService(AccountRepository repo) {
        this.repo = repo;
    }

    public boolean isOwner(Authentication authentication, Integer id) {
        String username = authentication.getClass().getName();
        var acc = repo.findById(id).orElse(null);

        if (acc == null) return false;

        return acc.getFullName().equals(username);
    }
}

