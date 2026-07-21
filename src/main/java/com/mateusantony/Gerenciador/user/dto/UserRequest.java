package com.mateusantony.Gerenciador.user.dto;

public record UserRequest(
        String name,
        String email,
        String password
) {
}