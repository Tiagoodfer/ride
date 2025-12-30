package com.ride.service;

import com.ride.config.JWTTokenProvider;
import com.ride.domain.User;
import com.ride.domain.enums.UserRole;
import com.ride.domain.enums.UserStatus;
import com.ride.dto.login.LoginRequestDTO;
import com.ride.dto.login.LoginResponseDTO;
import com.ride.dto.user.UserRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.info("Attempting login for CPF: {}", loginRequestDTO.cpf());
        User user = userRepository.findByCpf(loginRequestDTO.cpf())
                .orElseThrow(() -> new EntityNotFoundException("User not found with CPF: " + loginRequestDTO.cpf()));

        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("User is not active");
        }

        String token = jwtTokenProvider.generateJwtToken(user);
        return new LoginResponseDTO(user.getName(), user.getEmail(), token, user.getPhoneNumber());
    }

    public UserResponseDTO registerPassenger(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRole(UserRole.PASSENGER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponseDTO registerAdmin(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponseDTO registerDriver(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRole(UserRole.DRIVER);
        user.setStatus(UserStatus.PENDING_APPROVAL);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponseDTO registerInfluencer(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRole(UserRole.INFLUENCER);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    private void checkEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }
    }

    private void checkCpf(String cpf) {
        Optional<User> byCpf = userRepository.findByCpf(cpf);
        if (byCpf.isPresent()) {
            throw new IllegalArgumentException("User already exists with CPF: " + cpf);
        }
    }

    private User buildUser(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setCpf(dto.cpf());
        user.setPasswordHash(passwordEncoder.encode(dto.password()));
        user.setImageUrl(dto.imageUrl());
        user.setBalance(BigDecimal.ZERO);
        user.setPhoneNumber(null);
        return user;
    }

    private UserResponseDTO mapToResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }
}
