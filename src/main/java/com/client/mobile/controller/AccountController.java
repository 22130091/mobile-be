package com.client.mobile.controller;

import com.client.mobile.process_facade.AccountProfileFacade;
import com.client.mobile.dto.request.UpdateAccountRequest;
import com.client.mobile.dto.response.AccountResponse;
import com.client.mobile.service.imp.EmailOtpSender;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountProfileFacade accountFacade;

    public AccountController(AccountProfileFacade accountFacade, EmailOtpSender emailOtpSender) {
        this.accountFacade = accountFacade;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Integer id) {
        AccountResponse account = accountFacade.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountFacade.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @accountSecurityService.isOwner(authentication, #id)")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Integer id, @RequestBody UpdateAccountRequest dto) {
        AccountResponse updatedAccount = accountFacade.updateAccount(id, dto);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountFacade.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}