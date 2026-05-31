package com.loja.carros.config;

import com.loja.carros.entity.Perfil;
import com.loja.carros.exception.AcessoNegadoException;
import com.loja.carros.security.AuthContext;
import com.loja.carros.security.UsuarioAutenticado;
import com.loja.carros.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || rotaPublica(request)) {
            return true;
        }

        UsuarioAutenticado usuario = authService.autenticarToken(request.getHeader("Authorization"));
        AuthContext.setUsuario(usuario);

        if (rotaDeEscritaEmCarros(request) && usuario.perfil() != Perfil.ADMIN) {
            throw new AcessoNegadoException("Apenas administradores podem alterar veiculos");
        }

        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        AuthContext.limpar();
    }

    private boolean rotaPublica(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/cadastrar")
                || path.startsWith("/status");
    }

    private boolean rotaDeEscritaEmCarros(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        return path.startsWith("/carros")
                && ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method));
    }
}
