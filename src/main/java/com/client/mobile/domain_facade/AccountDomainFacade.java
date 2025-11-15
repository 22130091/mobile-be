package com.client.mobile.domain_facade;

import com.client.mobile.entity.Account;
import com.client.mobile.repository.AccountRepository;
import com.client.mobile.request.UpdateAccountRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountDomainFacade {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountDomainFacade(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccountData(Account account, String rawPassword) {
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setStatus("ACTIVE");
        return accountRepository.save(account);
    }

    public Account getAccountDataById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    public List<Account> getAllAccountData() {
        return accountRepository.findAll();
    }

    public Account updateAccountData(Integer id, UpdateAccountRequest dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setFullName(dto.getFullName());
        account.setPhone(dto.getPhone());
        account.setGender(dto.getGender());
        account.setDob(dto.getDob());
        account.setStatus(dto.getStatus());
        return accountRepository.save(account);
    }

    public void deleteAccountData(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }
}