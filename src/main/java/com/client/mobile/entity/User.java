package com.client.mobile.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;
    private String phone;

    @Column(nullable = false)
    private String passwordHash;

    private String role;      // customer, staff, manager, admin
    private String status;    // active, inactive, banned
}
