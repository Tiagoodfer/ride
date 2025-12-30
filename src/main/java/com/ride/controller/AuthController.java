package com.ride.controller;

import com.ride.dto.login.LoginRequestDTO;
import com.ride.dto.login.LoginResponseDTO;
import com.ride.dto.user.UserRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        log.info("Login: {}", loginRequestDTO.cpf());
        return authService.login(loginRequestDTO);
    }

    @PostMapping("/register")
    public UserResponseDTO registerPassenger(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Passenger: {}", userRequestDTO.email());
        return authService.registerPassenger(userRequestDTO);
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDTO registerAdmin(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Admin: {}", userRequestDTO.email());
        return authService.registerAdmin(userRequestDTO);
    }

    @PostMapping("/register/driver")
    public UserResponseDTO registerDriver(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Driver: {}", userRequestDTO.email());
        return authService.registerDriver(userRequestDTO);
    }

    @PostMapping("/register/influencer")
    public UserResponseDTO registerInfluencer(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        log.info("Register Influencer: {}", userRequestDTO.email());
        return authService.registerInfluencer(userRequestDTO);
    }

}
