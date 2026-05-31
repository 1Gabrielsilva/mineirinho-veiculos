package com.loja.carros.dto;

import java.math.BigDecimal;

public record CarroResponseDTO(
        Long id,
        String marca,
        String modelo,
        Integer ano,
        BigDecimal preco,
        String categoria,
        String tipoCarroceria,
        String cor,
        Integer quilometragem,
        String cambio
) {
}
