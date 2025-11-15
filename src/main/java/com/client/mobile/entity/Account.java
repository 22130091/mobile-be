package com.client.mobile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;
@Builder
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Add this annotation to exclude the collection
@EqualsAndHashCode(exclude = {"roles"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "gender", columnDefinition = "ENUM('Male','Female','Other') DEFAULT 'Other'")
    private String gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "status", columnDefinition = "ENUM('active','inactive','banned') DEFAULT 'active'")
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", insertable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Date updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}