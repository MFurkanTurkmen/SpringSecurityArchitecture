package com.mft.springsecurity.service;

import com.mft.springsecurity.dto.LoginRequest;
import com.mft.springsecurity.dto.LoginResponse;
import com.mft.springsecurity.dto.SignupRequest;
import com.mft.springsecurity.entity.Auth;
import com.mft.springsecurity.entity.LoginHistory;
import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.entity.enums.ERole;
import com.mft.springsecurity.exception.AuthenticationException;
import com.mft.springsecurity.repository.RoleRepository;
import com.mft.springsecurity.repository.AuthRepository;
import com.mft.springsecurity.util.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 30; // minutes


    public void register(SignupRequest request) {
        // Check if username already exists
        if (authRepository.findOptionalByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Create new user
        Auth user = new Auth();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
            Role roleUser= roleRepository.findOptionalByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            roles.add(roleUser);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":

                        Role roleAdmin= roleRepository.findOptionalByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(roleAdmin);
                        break;
                    case "mod":
                        Role roleMod= roleRepository.findOptionalByName(ERole.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        roles.add(roleMod);

                        break;
                    default:
                        Role roleUser= roleRepository.findOptionalByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        roles.add(roleUser);
                }
            });
        }


        user.setRoles(roles);
        authRepository.save(user);
        System.out.printf("User %s registered successfully!%n", user.getUsername());
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // 1. Validate and find user
        Auth user = authRepository.findOptionalByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException(
                        "Invalid username or password",
                        "AUTH_001",
                        HttpStatus.UNAUTHORIZED
                ));

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // Increment failed login attempts
            handleFailedLogin(user);
            throw new AuthenticationException(
                    "Invalid username or password",
                    "AUTH_001",
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 3. Check account status
        validateAccountStatus(user);

        // 4. Generate tokens
        String accessToken = jwtTokenManager.createToken(user.getUsername())
                .orElseThrow(() -> new AuthenticationException(
                        "Error generating token",
                        "AUTH_002",
                        HttpStatus.INTERNAL_SERVER_ERROR
                ));

        // 5. Update user's last login information
        updateLoginInfo(user, ipAddress);

        // 6. Build and return response
        return buildLoginResponse(user, accessToken);
    }


    private LoginResponse buildLoginResponse(Auth user, String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresAt(Instant.now().plusSeconds(3600)) // 1 hour
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .build())
                .build();
    }





    private void handleFailedLogin(Auth user) {
        // Başarısız giriş denemelerini takip etmek için yeni alanlar ekleyelim
        // Auth entity'sine bu alanları eklememiz gerekiyor:
        // private int failedAttempts;
        // private LocalDateTime lockTime;

        int failedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockTime(LocalDateTime.now());
            user.setAccountLocked(true);
        }

        authRepository.save(user);

        // Eğer hesap kilitlendi ise özel bir exception fırlatalım
        if (user.getAccountLocked()) {
            throw new AuthenticationException(
                    "Account has been locked due to multiple failed attempts. " +
                            "Please try again after " + LOCK_TIME_DURATION + " minutes.",
                    "AUTH_003",
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    private void validateAccountStatus(Auth user) {
        // Hesap kilitli mi kontrol et
        if (user.getAccountLocked()) {
            LocalDateTime lockTime = user.getLockTime();
            if (lockTime != null) {
                LocalDateTime unlockTime = lockTime.plusMinutes(LOCK_TIME_DURATION);
                if (LocalDateTime.now().isBefore(unlockTime)) {
                    long minutesRemaining = ChronoUnit.MINUTES.between(
                            LocalDateTime.now(),
                            unlockTime
                    );
                    throw new AuthenticationException(
                            "Account is locked. Please try again after " +
                                    minutesRemaining + " minutes.",
                            "AUTH_004",
                            HttpStatus.UNAUTHORIZED
                    );
                } else {
                    // Kilit süresini geçmiş, hesabı tekrar aktif et
                    user.setAccountLocked(false);
                    user.setFailedAttempts(0);
                    user.setLockTime(null);
                    authRepository.save(user);
                }
            }
        }

        // Hesap aktif mi kontrol et
        if (!user.getActive()) {
            throw new AuthenticationException(
                    "Account is not active. Please contact support.",
                    "AUTH_005",
                    HttpStatus.FORBIDDEN
            );
        }

        // Şifre değişikliği gerekiyor mu kontrol et
        if (user.getPasswordChangeRequired()) {
            throw new AuthenticationException(
                    "Password change is required. Please update your password.",
                    "AUTH_006",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private void updateLoginInfo(Auth user, String ipAddress) {
        // Başarılı giriş sonrası bilgileri güncelle
        user.setLastLoginDate(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setFailedAttempts(0); // Başarılı girişte deneme sayısını sıfırla
        user.setLockTime(null);
        user.setAccountLocked(false);

        // Son 5 girişi kaydet (circular buffer mantığıyla)
        List<LoginHistory> loginHistory = user.getLoginHistory();
        if (loginHistory == null) {
            loginHistory = new ArrayList<>();
        }

        LoginHistory newLogin = LoginHistory.builder()
                .loginDate(LocalDateTime.now())
                .ipAddress(ipAddress)
                .deviceInfo(getDeviceInfo()) // User-Agent bilgisini almak için yardımcı metot
                .successful(true)
                .build();

        loginHistory.add(0, newLogin); // En yeni girişi başa ekle

        // Sadece son 5 girişi tut
        if (loginHistory.size() > 5) {
            loginHistory = loginHistory.subList(0, 5);
        }

        user.setLoginHistory(loginHistory);
        authRepository.save(user);
    }

    // Yardımcı metotlar
    private String getDeviceInfo() {
        // ServletRequestAttributes attributes =
        //     (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // if (attributes != null) {
        //     HttpServletRequest request = attributes.getRequest();
        //     return request.getHeader("User-Agent");
        // }
        return "Unknown Device";
    }















}