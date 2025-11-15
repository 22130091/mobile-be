package com.client.mobile.process_facade;

import com.client.mobile.domain_facade.AccountDomainFacade;
import com.client.mobile.domain_facade.RoleDomainFacade;
import com.client.mobile.entity.Account;
import com.client.mobile.entity.Role;
import com.client.mobile.request.CreateAccountRequest;
import com.client.mobile.request.UpdateAccountRequest;
import com.client.mobile.response.AccountResponse;
import com.client.mobile.response.RoleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountProfileFacade {

    private final AccountDomainFacade accountDataFacade;
    private final RoleDomainFacade roleDataFacade;

    public AccountProfileFacade(AccountDomainFacade accountDataFacade,
                                RoleDomainFacade roleDataFacade) {
        this.accountDataFacade = accountDataFacade;
        this.roleDataFacade = roleDataFacade;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest dto) {
        Set<Role> roles = roleDataFacade.findRolesByNames(dto.getRoles());
        Account account = new Account();
        account.setEmail(dto.getEmail());
        account.setFullName(dto.getFullName());
        account.setPhone(dto.getPhone());
        account.setGender(dto.getGender());
        account.setDob(dto.getDob());
        account.setRoles(roles);
        Account savedAccount = accountDataFacade.createAccountData(account, dto.getPassword());
        return mapToResponse(savedAccount);
    }

    // READ (One) (Điều phối 1 domain facade)
    public AccountResponse getAccountById(Integer id) {
        Account account = accountDataFacade.getAccountDataById(id);
        return mapToResponse(account);
    }

    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountDataFacade.getAllAccountData();
        return accounts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse updateAccount(Integer id, UpdateAccountRequest dto) {
        Account updatedAccount = accountDataFacade.updateAccountData(id, dto);
        return mapToResponse(updatedAccount);
    }

    @Transactional
    public void deleteAccount(Integer id) {
        accountDataFacade.deleteAccountData(id);

        // notificationFacade.sendGoodbyeEmail(emailCuaUserVuaXoa);
    }


// Bên trong class AccountProfileFacade

    private AccountResponse mapToResponse(Account account) {
        AccountResponse res = new AccountResponse();
        res.setId(account.getId());
        res.setEmail(account.getEmail());
        res.setFullName(account.getFullName());
        res.setPhone(account.getPhone());
        res.setGender(account.getGender());
        res.setDob(account.getDob());
        res.setStatus(account.getStatus());
        res.setCreatedAt(account.getCreatedAt());
        res.setUpdatedAt(account.getUpdatedAt());

        if (account.getRoles() != null) {
            res.setRoles(
                    account.getRoles().stream()
                            .map(this::mapRoleToResponse)
                            .collect(Collectors.toSet())
            );
        }
        return res;
    }

    private RoleResponse mapRoleToResponse(Role role) {
        RoleResponse roleRes = new RoleResponse();
        roleRes.setId(role.getId());
        roleRes.setRoleName(role.getRoleName());
        return roleRes;
    }
}