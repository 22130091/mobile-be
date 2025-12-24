package com.client.mobile.domain_facade;

import com.client.mobile.entity.Account;
import com.client.mobile.repository.AccountRepository;
import com.client.mobile.dto.request.UpdateAccountRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
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
        Date now = new Date();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
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
        if (dto.getFullName() != null) account.setFullName(dto.getFullName());
        if (dto.getPhone() != null) account.setPhone(dto.getPhone());
        if (dto.getGender() != null) account.setGender(dto.getGender());
        if (dto.getDob() != null) account.setDob(dto.getDob());
        //if (dto.getAvatar() != null) account.setAvatar(dto.getAvatar());
        if (dto.getStatus() != null) account.setStatus(dto.getStatus());
        return accountRepository.save(account);
    }

    public void deleteAccountData(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }
}