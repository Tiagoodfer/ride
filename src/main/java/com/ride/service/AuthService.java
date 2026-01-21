package com.ride.service;

import com.ride.config.AuthenticatedUserUtils;
import com.ride.config.JWTTokenProvider;
import com.ride.domain.User;
import com.ride.domain.Wallet;
import com.ride.domain.enums.UserRole;
import com.ride.domain.enums.UserStatus;
import com.ride.domain.enums.WalletType;
import com.ride.dto.login.LoginRequestDTO;
import com.ride.dto.login.LoginResponseDTO;
import com.ride.dto.user.UserRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.repository.UserRepository;
import com.ride.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticatedUserUtils authenticatedUserUtils;
    private final FileStorageService fileStorageService;

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

    @Transactional
    public UserResponseDTO registerPassenger(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRoles(new HashSet<>(Set.of(UserRole.PASSENGER)));
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        createWallet(savedUser, WalletType.PASSENGER);

        return mapToResponse(savedUser);
    }

    @Transactional
    public UserResponseDTO registerAdmin(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRoles(new HashSet<>(Set.of(UserRole.ADMIN)));
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        createWallet(savedUser, WalletType.COMPANY);

        return mapToResponse(savedUser);
    }

    @Transactional
    public UserResponseDTO registerDriver(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRoles(new HashSet<>(Set.of(UserRole.DRIVER)));
        user.setStatus(UserStatus.PENDING_APPROVAL);

        User savedUser = userRepository.save(user);
        createWallet(savedUser, WalletType.DRIVER);

        return mapToResponse(savedUser);
    }

    @Transactional
    public UserResponseDTO registerInfluencer(UserRequestDTO userRequestDTO) {
        checkCpf(userRequestDTO.cpf());
        if (userRequestDTO.email() != null && !userRequestDTO.email().isBlank()) {
            checkEmail(userRequestDTO.email());
        }

        User user = buildUser(userRequestDTO);
        user.setRoles(new HashSet<>(Set.of(UserRole.INFLUENCER)));
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        createWallet(savedUser, WalletType.INFLUENCER);

        return mapToResponse(savedUser);
    }

    @Transactional
    public void addRoleDriver(MultipartFile cnhImage, MultipartFile carImage) {
        User user = authenticatedUserUtils.getUser();
        if (user.getRoles().contains(UserRole.DRIVER)) {
            throw new IllegalArgumentException("User is already a driver");
        }

        String cnhUrl = fileStorageService.uploadFile(cnhImage);
        String carUrl = fileStorageService.uploadFile(carImage);

        // TODO: Implementar validacao dos documentos

        user.setCnhImageUrl(cnhUrl);
        user.setCarDocumentImageUrl(carUrl);
        user.getRoles().add(UserRole.DRIVER);

        userRepository.save(user);
        createWallet(user, WalletType.DRIVER);
    }

    @Transactional
    public void addRoleInfluencer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        if (user.getRoles().contains(UserRole.INFLUENCER)) {
            throw new IllegalArgumentException("User is already an influencer");
        }
        user.getRoles().add(UserRole.INFLUENCER);
        userRepository.save(user);
        createWallet(user, WalletType.INFLUENCER);
    }

    @Transactional
    public void addRoleAdmin(String cpf) {
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException("User not found with CPF: " + cpf));

        if (user.getRoles().contains(UserRole.ADMIN)) {
            throw new IllegalArgumentException("User is already an admin");
        }
        user.getRoles().add(UserRole.ADMIN);
        userRepository.save(user);
        createWallet(user, WalletType.COMPANY);
    }

    private void createWallet(User user, WalletType type) {
        if (walletRepository.findByUserIdAndType(user.getId(), type).isPresent()) {
            return;
        }

        Wallet wallet = Wallet.builder()
                .userId(user.getId())
                .type(type)
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);
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
