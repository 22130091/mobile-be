package com.client.mobile.config.security;

import com.client.mobile.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;




                                // Tuyệt đối không xóa file này. Bean này dùng để kiểm tra is owner dựa trên fullName




@Service("accountSecurityService")
public class AccountSecurityService {

    private final AccountRepository repo;

    public AccountSecurityService(AccountRepository repo) {
        this.repo = repo;
    }

    public boolean isOwner(Authentication authentication, Integer id) {

        String username = authentication.getName();

        var acc = repo.findById(id).orElse(null);
        if (acc == null) return false;

        return acc.getFullName().equals(username);
    }
}
