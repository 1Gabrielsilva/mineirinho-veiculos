package com.loja.carros.dto;

public record AuthResponseDTO(
        String token,
        String nome,
        String username,
        String perfil
) {
}
