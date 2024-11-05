package com.mft.springsecurity.service;

import com.mft.springsecurity.dto.UserRQ;
import com.mft.springsecurity.entity.User;
import com.mft.springsecurity.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(UserRQ userDto) {
        User user= new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User update(User user) {
        if (userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}