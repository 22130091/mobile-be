package com.client.mobile.repository;

import com.client.mobile.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Optional<Permission> findByPermissionName(String permissionName);

    boolean existsByPermissionName(String permissionName);
}