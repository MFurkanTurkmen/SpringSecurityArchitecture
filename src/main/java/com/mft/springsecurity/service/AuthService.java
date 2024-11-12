package com.mft.springsecurity.service;

import com.mft.springsecurity.dto.LoginRequest;
import com.mft.springsecurity.dto.LoginResponse;
import com.mft.springsecurity.dto.SignupRequest;
import com.mft.springsecurity.entity.MyUser;
import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.entity.enums.ERole;
import com.mft.springsecurity.exception.AuthenticationException;
import com.mft.springsecurity.repository.RoleRepository;
import com.mft.springsecurity.repository.UserRepository;
import com.mft.springsecurity.util.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;


    public void register(SignupRequest request) {
        // Check if username already exists
        if (userRepository.findOptionalByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        // Create new user
        MyUser user = new MyUser();
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
        userRepository.save(user);
        System.out.printf("User %s registered successfully!%n", user.getUsername());
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        // 1. Validate and find user
        MyUser user = userRepository.findOptionalByUsername(request.getUsername())
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

    private void handleFailedLogin(MyUser user) {
        // Implement failed login attempt tracking
        // You might want to store this in a separate table or cache
        // And implement account locking after X failed attempts
    }

    private void validateAccountStatus(MyUser user) {
        // Add additional validation logic here
        // Example: check if account is locked, expired, or requires password change
    }

    private void updateLoginInfo(MyUser user, String ipAddress) {
        // Update last login date and IP
        // You might want to add these fields to your User entity
        userRepository.save(user);
    }

    private LoginResponse buildLoginResponse(MyUser user, String accessToken) {
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
}