package com.mft.springsecurity.service;

import com.mft.springsecurity.dto.SignupRequest;
import com.mft.springsecurity.entity.MyUser;
import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.entity.enums.ERole;
import com.mft.springsecurity.repository.RoleRepository;
import com.mft.springsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
            Role roleUser= roleRepository.findOptionalByName(ERole.ROLE_USER.name()).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

            roles.add(roleUser);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":

                        Role roleAdmin= roleRepository.findOptionalByName(ERole.ROLE_ADMIN.name()).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(roleAdmin);
                        break;
                    case "mod":
                        Role roleMod= roleRepository.findOptionalByName(ERole.ROLE_MODERATOR.name()).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        roles.add(roleMod);

                        break;
                    default:
                        Role roleUser= roleRepository.findOptionalByName(ERole.ROLE_USER.name()).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

                        roles.add(roleUser);
                }
            });
        }


        user.setRoles(roles);
        userRepository.save(user);
        System.out.printf("User %s registered successfully!%n", user.getUsername());
    }
}