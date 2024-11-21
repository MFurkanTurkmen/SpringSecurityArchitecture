package com.mft.springsecurity.service;

import com.mft.springsecurity.dto.request.LoginRequest;
import com.mft.springsecurity.dto.response.LoginResponse;
import com.mft.springsecurity.dto.request.SignupRequest;
import com.mft.springsecurity.entity.Auth;
import com.mft.springsecurity.entity.LoginHistory;
import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.entity.enums.ERole;
import com.mft.springsecurity.exception.AllExceptions;
import com.mft.springsecurity.exception.AuthenticationException;
import com.mft.springsecurity.mapper.AuthMapper;
import com.mft.springsecurity.repository.RoleRepository;
import com.mft.springsecurity.repository.AuthRepository;
import com.mft.springsecurity.util.JwtTokenManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

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
        if (authRepository.findOptionalByUsername(request.getUsername()).isPresent()) {
            throw new AuthenticationException(AllExceptions.ERROR_400,"username already exists");
        }

        Auth auth= AuthMapper.INSTANCE.map(request);

        auth.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = request.getRole().stream()
                .map(role -> roleRepository.findOptionalByName(ERole.valueOf(role))
                        .orElseThrow(() -> new AuthenticationException(AllExceptions.ERROR_400," Role Not Fount")))
                .collect(Collectors.toSet());

        auth.setRoles(roles);

        authRepository.save(auth);
    }

    public LoginResponse login(LoginRequest request) {
        Auth auth = authRepository.findOptionalByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException(AllExceptions.ERROR_400,"User not found"));

        if (!passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
            throw new AuthenticationException(AllExceptions.ERROR_400,"Password is incorrect");
        }

        String accessToken = jwtTokenManager.createToken(auth.getUsername())
                .orElseThrow(() -> new AuthenticationException(AllExceptions.ERROR_400));


        return LoginResponse.builder().bearer(accessToken).build();
    }



}