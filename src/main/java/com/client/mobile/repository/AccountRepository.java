package com.client.mobile.repository;

import com.client.mobile.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByFullName(String fullName);
    List<Account> findAllByFullName(String fullName);

    Optional<Account> findByPhone(String phoneNumber);
}
