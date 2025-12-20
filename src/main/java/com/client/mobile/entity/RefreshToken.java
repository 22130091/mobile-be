package com.client.mobile.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "user_id")
    private Account account;

    @Column(name = "device_info")
    private String deviceInfo;

    private boolean revoked;
}