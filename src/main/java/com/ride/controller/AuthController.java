package com.ride.controller;

import com.ride.dto.login.LoginRequestDTO;
import com.ride.dto.login.LoginResponseDTO;
import com.ride.dto.user.UserRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        log.info("Login: {}", loginRequestDTO.cpf());
        return authService.login(loginRequestDTO);
    }

    @Operation(summary = "Register a new Passenger")
    @PostMapping("/register")
    public UserResponseDTO registerPassenger(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Passenger: {}", userRequestDTO.email());
        return authService.registerPassenger(userRequestDTO);
    }

    @Operation(summary = "Register a new Admin (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO registerAdmin(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Admin: {}", userRequestDTO.email());
        return authService.registerAdmin(userRequestDTO);
    }

    @Operation(summary = "Register a new Driver")
    @PostMapping("/register/driver")
    public UserResponseDTO registerDriver(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Driver: {}", userRequestDTO.email());
        return authService.registerDriver(userRequestDTO);
    }

    @Operation(summary = "Register a new Influencer")
    @PostMapping("/register/influencer")
    public UserResponseDTO registerInfluencer(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Influencer: {}", userRequestDTO.email());
        return authService.registerInfluencer(userRequestDTO);
    }

    @Operation(summary = "Add Driver role to current user with documents")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/role/driver", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addRoleDriver(
            @RequestParam("cnh") MultipartFile cnh,
            @RequestParam("carDocument") MultipartFile carDocument) {
        log.info("Adding Driver role to current user with documents");
        authService.addRoleDriver(cnh, carDocument);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add Influencer role to a specific user (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/influencer/{userId}")
    public ResponseEntity<Void> addRoleInfluencer(@PathVariable UUID userId) {
        log.info("Adding Influencer role to user ID: {}", userId);
        authService.addRoleInfluencer(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add Admin role to a specific user (Admin only)")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/admin/{cpf}")
    public ResponseEntity<Void> addRoleAdmin(@PathVariable String cpf) {
        log.info("Adding Admin role to user with CPF: {}", cpf);
        authService.addRoleAdmin(cpf);
        return ResponseEntity.ok().build();
    }

}
