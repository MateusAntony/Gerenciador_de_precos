package com.mateusantony.Gerenciador.user.dto;

public record UserResponse(
        Long id,
        String name,
        String email
) {
}