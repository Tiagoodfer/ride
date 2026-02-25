package com.ride.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.br.CPF;

public record UserRequestDTO(
        @Schema(description = "User's full name", example = "Passenger User")
        String name,

        @Schema(description = "User's email address", example = "passenger@example.com")
        String email,

        @Schema(description = "User's CPF (digits only or formatted)", example = "111.111.111-11")
        @CPF
        String cpf,

        @Schema(description = "User's password", example = "hash123")
        String password,

        @Schema(description = "User's profile image URL", example = "http://img.com/1")
        String imageUrl
) {
}
