package com.loja.carros.controller;

import com.loja.carros.dto.AuthResponseDTO;
import com.loja.carros.dto.CadastroUsuarioRequestDTO;
import com.loja.carros.dto.LoginRequestDTO;
import com.loja.carros.dto.UsuarioLogadoDTO;
import com.loja.carros.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<AuthResponseDTO> cadastrar(@RequestBody CadastroUsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrarCliente(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioLogadoDTO> me() {
        return ResponseEntity.ok(authService.usuarioLogado());
    }
}
