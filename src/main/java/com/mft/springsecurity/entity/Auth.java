package com.mft.springsecurity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name",  length = 100)
    private String name;

    @Column(name = "surname",  length = 100)
    private String surname;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "age")
    private Integer age;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();





    @Column(name = "account_locked")
    private Boolean accountLocked = false; // varsayılan değer false

    @Column(name = "failed_attempts")
    private int failedAttempts = 0; // varsayılan değer 0

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "active")
    private Boolean active = true; // varsayılan değer true

    @Column(name = "password_change_required")
    private Boolean passwordChangeRequired = false; // varsayılan değer false

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<LoginHistory> loginHistory;
}