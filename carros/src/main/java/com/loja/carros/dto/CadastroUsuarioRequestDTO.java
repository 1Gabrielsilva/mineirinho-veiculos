package com.loja.carros.dto;

public record CadastroUsuarioRequestDTO(
        String nome,
        String username,
        String senha
) {
}
