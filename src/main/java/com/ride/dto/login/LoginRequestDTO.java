package com.ride.dto.login;

import org.hibernate.validator.constraints.br.CPF;

public record LoginRequestDTO(@CPF String cpf, String password) {

}
