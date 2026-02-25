package com.ride.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserPhoneNumberRequestDTO(
        @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "User's phone number", example = "+5511999999999")
        String phoneNumber
) {
}
