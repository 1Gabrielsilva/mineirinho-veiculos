package com.loja.carros.controller;

import com.loja.carros.dto.CarroRequestDTO;
import com.loja.carros.dto.CarroResponseDTO;
import com.loja.carros.service.CarroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/carros")
@RequiredArgsConstructor
public class CarroController {

    private final CarroService carroService;

    @GetMapping
    public ResponseEntity<List<CarroResponseDTO>> listar() {
        return ResponseEntity.ok(carroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(carroService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CarroResponseDTO> cadastrar(@RequestBody CarroRequestDTO dto) {
        CarroResponseDTO carroCriado = carroService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(carroCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarroResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody CarroRequestDTO dto
    ) {
        return ResponseEntity.ok(carroService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        carroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
