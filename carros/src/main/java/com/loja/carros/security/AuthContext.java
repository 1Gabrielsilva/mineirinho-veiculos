package com.loja.carros.security;

import java.util.Optional;

public final class AuthContext {

    private static final ThreadLocal<UsuarioAutenticado> USUARIO_LOGADO = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void setUsuario(UsuarioAutenticado usuario) {
        USUARIO_LOGADO.set(usuario);
    }

    public static Optional<UsuarioAutenticado> getUsuario() {
        return Optional.ofNullable(USUARIO_LOGADO.get());
    }

    public static void limpar() {
        USUARIO_LOGADO.remove();
    }
}
