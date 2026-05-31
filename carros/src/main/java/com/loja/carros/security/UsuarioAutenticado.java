package com.loja.carros.security;

import com.loja.carros.entity.Perfil;

public record UsuarioAutenticado(
        Long id,
        String nome,
        String username,
        Perfil perfil
) {
}
