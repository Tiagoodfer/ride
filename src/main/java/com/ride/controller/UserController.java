package com.ride.controller;

import com.ride.domain.User;
import com.ride.domain.enums.UserStatus;
import com.ride.dto.user.UserGetResponseDTO;
import com.ride.dto.user.UserPhoneNumberRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.dto.user.UserUpdateDTO;
import com.ride.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/addphonenumber/{id}")
    public void addPhoneNumber(@PathVariable Long id, @RequestBody UserPhoneNumberRequestDTO phoneNumber) {
        log.info("Add Phone Number: {}", phoneNumber);
        userService.updateUserAddPhoneNumber(id, phoneNumber);
    }

    @PostMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/me")
    public UserGetResponseDTO getUser() {
        return userService.findUser();
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Update User: {}", userUpdateDTO.name());
        return ResponseEntity.ok(userService.updateUser(userUpdateDTO));
    }

    @PatchMapping("/status/active")
    public ResponseEntity<Void> setUserStatusActive() {
        userService.setUserStatus(UserStatus.ACTIVE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/status/blocked")
    public ResponseEntity<Void> setUserStatusBlocked() {
        userService.setUserStatus(UserStatus.BLOCKED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
