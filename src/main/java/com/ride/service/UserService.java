package com.ride.service;

import com.ride.config.AuthenticatedUserUtils;
import com.ride.domain.User;
import com.ride.domain.enums.UserStatus;
import com.ride.dto.user.UserGetResponseDTO;
import com.ride.dto.user.UserPhoneNumberRequestDTO;
import com.ride.dto.user.UserResponseDTO;
import com.ride.dto.user.UserUpdateDTO;
import com.ride.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticatedUserUtils authUtils;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserGetResponseDTO findUser() {
        User user = authUtils.getUser();
        return new UserGetResponseDTO(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Transactional
    public void updateUserAddPhoneNumber(Long id, UserPhoneNumberRequestDTO phoneNumber) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setPhoneNumber(phoneNumber.phoneNumber());
        userRepository.save(user);
    }

    @Transactional
    public UserResponseDTO updateUser(UserUpdateDTO userUpdateDTO) {
        User currentUser = authUtils.getUser();
        currentUser.setName(userUpdateDTO.name());

        if (userUpdateDTO.phoneNumber() != null) {
            currentUser.setPhoneNumber(userUpdateDTO.phoneNumber());
        }

        User updatedUser = userRepository.save(currentUser);
        return new UserResponseDTO(updatedUser.getId(), updatedUser.getName(), updatedUser.getEmail());
    }

    @Transactional
    public void setUserStatus(UserStatus status) {
        User user = authUtils.getUser();
        user.setStatus(status);
        userRepository.save(user);
        log.info("User status updated to {} for user {}", status, user.getCpf());
    }

}
