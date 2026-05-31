package com.loja.carros.config;

import com.loja.carros.entity.Perfil;
import com.loja.carros.repository.UsuarioRepository;
import com.loja.carros.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DadosIniciaisConfig {

    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    @Bean
    public CommandLineRunner criarUsuariosPadrao() {
        return args -> {
            if (!usuarioRepository.existsByUsername("admin")) {
                usuarioRepository.save(authService.criarUsuario(
                        "Administrador",
                        "admin",
                        "admin123",
                        Perfil.ADMIN
                ));
            }

            if (!usuarioRepository.existsByUsername("cliente")) {
                usuarioRepository.save(authService.criarUsuario(
                        "Cliente",
                        "cliente",
                        "cliente123",
                        Perfil.CLIENTE
                ));
            }
        };
    }
}
