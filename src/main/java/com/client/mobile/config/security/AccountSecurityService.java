package com.client.mobile.config.security;

import com.client.mobile.entity.Account;
import com.client.mobile.repository.AccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("accountSecurityService")
public class AccountSecurityService {

    private final AccountRepository accountRepository;

    public AccountSecurityService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Kiểm tra xem người dùng có phải là Admin hoặc là chủ sở hữu của tài khoản có ID tương ứng hay không.
     * @param authentication Đối tượng xác thực hiện tại.
     * @param id ID của tài khoản cần kiểm tra quyền sở hữu.
     * @return true nếu là Admin hoặc là chủ sở hữu, ngược lại là false.
     */
    public boolean isOwner(Authentication authentication, Integer id) {
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println(authority.getAuthority());
            if (authority.getAuthority().equals("ADMIN")) {
                return true;
            }
        }
        String username = authentication.getName();
        Account account = accountRepository.findByFullName(username)
                .orElse(null);

        if (account == null) {
            return false;
        }

        return account.getId().equals(id);
    }
}