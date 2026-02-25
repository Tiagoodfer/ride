package com.ride.controller;

import com.ride.domain.User;
import com.ride.domain.enums.UserStatus;
import com.ride.dto.user.UserGetResponseDTO;
import com.ride.dto.user.UserPhoneNumberRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.dto.user.UserUpdateDTO;
import com.ride.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @Operation(summary = "Add phone number to a user")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/addphonenumber")
    public void addPhoneNumber(@RequestBody UserPhoneNumberRequestDTO phoneNumber) {
        log.info("Add Phone Number: {}", phoneNumber);
        userService.updateUserAddPhoneNumber(phoneNumber);
    }

    @Operation(summary = "Get all users (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get current authenticated user details")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public UserGetResponseDTO getUser() {
        return userService.findUser();
    }

    @Operation(summary = "Update current user details")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/update")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("Update User: {}", userUpdateDTO.name());
        return ResponseEntity.ok(userService.updateUser(userUpdateDTO));
    }

    @Operation(summary = "Set current user status to ACTIVE")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/status/active")
    public ResponseEntity<Void> setUserStatusActive() {
        userService.setUserStatus(UserStatus.ACTIVE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Set current user status to BLOCKED")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/status/blocked")
    public ResponseEntity<Void> setUserStatusBlocked() {
        userService.setUserStatus(UserStatus.BLOCKED);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
