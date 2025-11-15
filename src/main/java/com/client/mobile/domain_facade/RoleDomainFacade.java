package com.client.mobile.domain_facade;

import com.client.mobile.entity.Role;
import com.client.mobile.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleDomainFacade {

    private final RoleRepository roleRepository;

    public RoleDomainFacade(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Set<Role> findRolesByNames(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new HashSet<>();
        }

        Set<Role> foundRoles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            foundRoles.add(role);
        }
        return foundRoles;
    }
}
