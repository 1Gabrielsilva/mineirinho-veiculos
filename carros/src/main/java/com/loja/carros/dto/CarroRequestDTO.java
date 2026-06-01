package com.loja.carros.dto;

import java.math.BigDecimal;

public record CarroRequestDTO(
        String marca,
        String modelo,
        Integer ano,
        BigDecimal preco,
        String cidade,
        String tipoCarroceria,
        String cor,
        Integer quilometragem,
        String cambio,
        String imagemPath
) {
}
