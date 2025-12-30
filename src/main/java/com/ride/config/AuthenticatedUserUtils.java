package com.ride.config;

import com.ride.domain.User;
import com.ride.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserUtils {

    private final UserRepository userRepository;

    public String getCpf() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found in security context.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String cpf) {
            return cpf;
        }

        throw new IllegalStateException("Could not extract user CPF from authentication principal.");
    }

    public User getUser() {
        return userRepository.findByCpf(getCpf())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database."));
    }
}
