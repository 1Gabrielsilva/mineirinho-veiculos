package com.loja.carros.repository;

import com.loja.carros.entity.SessaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessaoUsuarioRepository extends JpaRepository<SessaoUsuario, Long> {

    Optional<SessaoUsuario> findByToken(String token);
}
