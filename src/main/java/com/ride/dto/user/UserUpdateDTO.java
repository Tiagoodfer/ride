package com.ride.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateDTO(
        @Schema(description = "User's full name", example = "Passenger User")
        String name,

        @Schema(description = "User's phone number", example = "+5511999999999")
        String phoneNumber
) {
}
