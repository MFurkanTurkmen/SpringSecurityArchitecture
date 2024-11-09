package com.mft.springsecurity.controller;

import com.mft.springsecurity.dto.UserRQ;
import com.mft.springsecurity.dto.UserRS;
import com.mft.springsecurity.entity.MyUser;
import com.mft.springsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserRS> getAllUsers() {
        return userService.findAll().stream()
                .map(user -> new UserRS(user.getId(), user.getName(), user.getSurname(), user.getBirthdate(), user.getAge(), user.getCity()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRS> getUserById(@PathVariable Long id) {
        Optional<MyUser> user = userService.findById(id);
        return user.map(value -> ResponseEntity.ok(new UserRS(value.getId(), value.getName(), value.getSurname(), value.getBirthdate(), value.getAge(), value.getCity())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserRS> createUser(@RequestBody UserRQ user) {
        MyUser savedUser = userService.save(user);
        return ResponseEntity.ok(new UserRS(savedUser.getId(), savedUser.getName(), savedUser.getSurname(), savedUser.getBirthdate(), savedUser.getAge(), savedUser.getCity()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRS> updateUser(@PathVariable Long id, @RequestBody MyUser user) {
        user.setId(id);
        MyUser updatedUser = userService.update(user);
        return ResponseEntity.ok(new UserRS(updatedUser.getId(), updatedUser.getName(), updatedUser.getSurname(), updatedUser.getBirthdate(), updatedUser.getAge(), updatedUser.getCity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}