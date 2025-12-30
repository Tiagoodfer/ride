package com.ride.dto.user;

import org.hibernate.validator.constraints.br.CPF;

public record UserRequestDTO(String name, String email, @CPF String cpf, String password, String imageUrl) {
}
